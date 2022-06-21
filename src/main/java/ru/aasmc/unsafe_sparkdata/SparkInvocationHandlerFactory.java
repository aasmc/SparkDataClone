package ru.aasmc.unsafe_sparkdata;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import ru.aasmc.unsafe_sparkdata.dataextractors.DataExtractor;
import ru.aasmc.unsafe_sparkdata.dataextractors.DataExtractorResolver;
import ru.aasmc.unsafe_sparkdata.finalizers.Finalizer;
import ru.aasmc.unsafe_sparkdata.finalizers.LazyCollectionInjectorPostFinalizer;
import ru.aasmc.unsafe_sparkdata.transformations.SparkTransformation;
import ru.aasmc.unsafe_sparkdata.transformations.spider.TransformationSpider;
import ru.aasmc.unsafe_sparkdata.util.Source;
import ru.aasmc.unsafe_sparkdata.util.Transient;
import ru.aasmc.unsafe_sparkdata.util.WordsMatcher;
import scala.Tuple2;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SparkInvocationHandlerFactory {


    private final DataExtractorResolver resolver;
    private final Map<String, TransformationSpider> spiderMap;
    private final Map<String, Finalizer> finalizerMap;

    @Setter
    private ConfigurableApplicationContext realContext;

    public SparkInvocationHandler create(Class<? extends SparkRepository> sparkRepoInterface) {
        Class<?> modelClass = getModelClass(sparkRepoInterface);
        String pathToData = modelClass.getAnnotation(Source.class).value();
        Set<String> fieldNames = getFieldNames(modelClass);
        DataExtractor dataExtractor = resolver.resolve(pathToData);

        Map<Method, List<Tuple2<SparkTransformation, List<String>>>> transformationChain = new HashMap<>();
        Map<Method, Finalizer> methodToFinalizer = new HashMap<>();

        Method[] methods = sparkRepoInterface.getMethods();
        for (Method method : methods) {
            TransformationSpider currentSpider = null;
            List<String> methodWords = WordsMatcher.toWordsByJavaConvention(method.getName());
            List<Tuple2<SparkTransformation, List<String>>> transformations = new ArrayList<>();
            while (methodWords.size() > 1) {
                String spiderName = WordsMatcher.findAndRemoveMatchingPiecesIfExists(spiderMap.keySet(), methodWords);
                if (!spiderName.isEmpty()) {
                    currentSpider = spiderMap.get(spiderName);
                }
                transformations.add(currentSpider.getTransformation(methodWords, fieldNames));
            }
            transformationChain.put(method, transformations);
            String finalizerName = "collect";
            if (methodWords.size() == 1) {
                finalizerName = Introspector.decapitalize(methodWords.get(0));
            }
            Finalizer finalizer = finalizerMap.get(finalizerName);
            methodToFinalizer.put(method, finalizer);
        }

        return SparkInvocationHandler.builder()
                .modelClass(modelClass)
                .dataExtractor(dataExtractor)
                .pathToData(pathToData)
                .transformationChain(transformationChain)
                .context(realContext)
                .finalizerMap(methodToFinalizer)
                .postFinalizer(new LazyCollectionInjectorPostFinalizer(realContext))
                .build();
    }

    private Class<?> getModelClass(Class<? extends SparkRepository> repoInterface) {
        ParameterizedType genericInterface = (ParameterizedType) repoInterface.getGenericInterfaces()[0];
        Class<?> modelClass = (Class<?>) genericInterface.getActualTypeArguments()[0];
        return modelClass;
    }

    private Set<String> getFieldNames(Class<?> modelClass) {
        return Arrays.stream(modelClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Collection.class.isAssignableFrom(field.getType()))
                .map(Field::getName)
                .collect(Collectors.toSet());
    }
}
