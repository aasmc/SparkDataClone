package ru.aasmc.unsafe_sparkdata.transformations;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import ru.aasmc.unsafe_sparkdata.util.OrderedBag;

import java.util.List;

public class SortTransformation implements SparkTransformation {
    @Override
    public Dataset<Row> transform(Dataset<Row> dataset, List<String> colNames, OrderedBag<Object> args) {
        return dataset.orderBy(colNames.get(0), colNames.stream().skip(1).toArray(String[]::new));
    }
}
