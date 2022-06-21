package ru.aasmc.unsafe_sparkdata.transformations;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import ru.aasmc.unsafe_sparkdata.util.OrderedBag;

import java.util.List;

public interface SparkTransformation {
    Dataset<Row> transform(Dataset<Row> dataset, List<String> colNames, OrderedBag<Object> args);
}
