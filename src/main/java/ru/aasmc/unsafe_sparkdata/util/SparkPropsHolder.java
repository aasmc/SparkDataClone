package ru.aasmc.unsafe_sparkdata.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "spark")
@ConfigurationPropertiesScan
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SparkPropsHolder {
    private String appName;
    private String packagesToScan;
}
