package ru.aasmc.unsafe_sparkdata;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import ru.aasmc.unsafe_sparkdata.dataextractors.DataExtractorResolver;

import java.beans.Introspector;
import java.lang.reflect.Proxy;
import java.util.Set;

public class SparkApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        AnnotationConfigApplicationContext tmpContext = new AnnotationConfigApplicationContext("ru.aasmc.unsafe_sparkdata");
        SparkInvocationHandlerFactory factory = tmpContext.getBean(SparkInvocationHandlerFactory.class);
        DataExtractorResolver extractorResolver = tmpContext.getBean(DataExtractorResolver.class);
        context.getBeanFactory().registerSingleton("dataExtractorResolver", extractorResolver);
        tmpContext.close();

        factory.setRealContext(context);

        registerSparkBeans(context);
        String packagesToScan = context.getEnvironment().getProperty("spark.packages-to-scan");
        Reflections scanner = new Reflections(packagesToScan);
        Set<Class<? extends SparkRepository>> sparkRepoInterfaces = scanner.getSubTypesOf(SparkRepository.class);
        sparkRepoInterfaces.forEach(sparkRepoInterface -> {
            Object golem = Proxy.newProxyInstance(
                    sparkRepoInterface.getClassLoader(),
                    new Class[]{sparkRepoInterface},
                    factory.create(sparkRepoInterface)
            );
            context.getBeanFactory().
                    registerSingleton(Introspector.decapitalize(sparkRepoInterface.getSimpleName()), golem);
        });
    }

    private void registerSparkBeans(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        String appName = environment.getProperty("spark.app-name");
        SparkSession sparkSession = SparkSession.builder().appName(appName).master("local[*]").getOrCreate();
        JavaSparkContext sparkContext = new JavaSparkContext(sparkSession.sparkContext());
        context.getBeanFactory().registerSingleton("sparkContext", sparkContext);
        context.getBeanFactory().registerSingleton("sparkSession", sparkSession);
    }
}
