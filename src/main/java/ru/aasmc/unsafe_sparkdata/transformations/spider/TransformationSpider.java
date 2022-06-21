package ru.aasmc.unsafe_sparkdata.transformations.spider;

import ru.aasmc.unsafe_sparkdata.transformations.SparkTransformation;
import scala.Tuple2;

import java.util.List;
import java.util.Set;

public interface TransformationSpider {
    Tuple2<SparkTransformation, List<String>> getTransformation(List<String> methodWords, Set<String> fieldNames);
}
