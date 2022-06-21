package ru.aasmc.unsafe_sparkdata.lazy;

import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import ru.aasmc.unsafe_sparkdata.dataextractors.DataExtractor;
import ru.aasmc.unsafe_sparkdata.dataextractors.DataExtractorResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstLevelCacheService {

    /**
     * Map that stores model classes to cold reusable stream-like [Dataset]s.
     */
    private Map<Class<?>, Dataset<Row>> modelToDataset = new HashMap<>();

    @Autowired
    private DataExtractorResolver extractorResolver;

    public List readDataFor(long ownerId,
                                    Class<?> modelClass,
                                    String pathToSource,
                                    String foreignKeyName,
                                    ConfigurableApplicationContext context) {
        if (!modelToDataset.containsKey(modelClass)) {
            DataExtractor extractor = extractorResolver.resolve(pathToSource);
            Dataset<Row> dataset = extractor.load(pathToSource, context);
            dataset.persist();
            modelToDataset.put(modelClass, dataset);
        }
        Encoder<?> encoder = Encoders.bean(modelClass);
        return modelToDataset.get(modelClass).filter(functions.col(foreignKeyName).equalTo(ownerId))
                .as(encoder).collectAsList();
    }
}
