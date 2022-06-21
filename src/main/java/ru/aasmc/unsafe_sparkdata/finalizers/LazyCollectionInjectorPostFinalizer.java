package ru.aasmc.unsafe_sparkdata.finalizers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ConfigurableApplicationContext;
import ru.aasmc.unsafe_sparkdata.lazy.LazySparkList;
import ru.aasmc.unsafe_sparkdata.util.ForeignKey;
import ru.aasmc.unsafe_sparkdata.util.Source;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class LazyCollectionInjectorPostFinalizer implements PostFinalizer {

    private final ConfigurableApplicationContext realContext;

    @SneakyThrows
    @Override
    public Object postFinalize(Object retVal) {
        if (Collection.class.isAssignableFrom(retVal.getClass())) {
            for (Object model : (List) retVal) {
                Field idField = model.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                long ownerId = idField.getLong(model);
                Field[] fields = model.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (List.class.isAssignableFrom(field.getType())) {
                        LazySparkList sparkList = realContext.getBean(LazySparkList.class);
                        sparkList.setOwnerId(ownerId);
                        String columnName = field.getAnnotation(ForeignKey.class).value();
                        sparkList.setForeignKeyName(columnName);
                        Class<?> embeddedModel = getEmbeddedModel(field);
                        sparkList.setModelClass(embeddedModel);
                        String pathToData = embeddedModel.getAnnotation(Source.class).value();
                        sparkList.setPathToSource(pathToData);

                        field.setAccessible(true);
                        field.set(model, sparkList);
                    }
                }
            }
        }
        return retVal;
    }

    private Class<?> getEmbeddedModel(Field field) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        return (Class<?>) genericType.getActualTypeArguments()[0];
    }
}
