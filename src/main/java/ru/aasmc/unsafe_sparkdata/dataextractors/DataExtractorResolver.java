package ru.aasmc.unsafe_sparkdata.dataextractors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataExtractorResolver {

    @Autowired
    Map<String, DataExtractor> dataExtractorMap;

    public DataExtractor resolve(String pathToData) {
        String fileExtension = pathToData.split("\\.")[1];
        return dataExtractorMap.get(fileExtension);
    }
}
