package ru.aasmc.unsafe_sparkdata;

import lombok.Builder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.context.ConfigurableApplicationContext;
import ru.aasmc.unsafe_sparkdata.dataextractors.DataExtractor;
import ru.aasmc.unsafe_sparkdata.finalizers.Finalizer;
import ru.aasmc.unsafe_sparkdata.finalizers.PostFinalizer;
import ru.aasmc.unsafe_sparkdata.transformations.SparkTransformation;
import ru.aasmc.unsafe_sparkdata.util.OrderedBag;
import scala.Tuple2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Builder
public class SparkInvocationHandler implements InvocationHandler {

    private Class<?> modelClass;
    private String pathToData;
    private DataExtractor dataExtractor;
    private Map<Method, List<Tuple2<SparkTransformation, List<String>>>> transformationChain;
    private Map<Method, Finalizer> finalizerMap;
    private ConfigurableApplicationContext context;
    private PostFinalizer postFinalizer;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Dataset<Row> dataset = dataExtractor.load(pathToData, context);
        List<Tuple2<SparkTransformation, List<String>>> tupleList = transformationChain.get(method);
        OrderedBag<Object> arguments = new OrderedBag<>(args);
        for (Tuple2<SparkTransformation, List<String>> tuple : tupleList) {
            SparkTransformation transformation = tuple._1();
            List<String> colNames = tuple._2();
            dataset = transformation.transform(dataset, colNames, arguments);
        }
        Finalizer finalizer = finalizerMap.get(method);
        Object retVal = finalizer.doAction(dataset, modelClass);
        return postFinalizer.postFinalize(retVal);
    }
}
