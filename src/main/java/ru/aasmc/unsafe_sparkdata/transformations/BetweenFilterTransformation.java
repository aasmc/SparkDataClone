package ru.aasmc.unsafe_sparkdata.transformations;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Component;
import ru.aasmc.unsafe_sparkdata.util.OrderedBag;

import java.util.List;

@Component("between")
public class BetweenFilterTransformation implements FilterTransformation {
    @Override
    public Dataset<Row> transform(Dataset<Row> dataset, List<String> colNames, OrderedBag<Object> args) {
        return dataset.filter(functions.col(colNames.get(0)).between(args.takeAndRemove(), args.takeAndRemove()));
    }
}
