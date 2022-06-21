package ru.aasmc.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.aasmc.unsafe_sparkdata.lazy.FirstLevelCacheService;
import ru.aasmc.unsafe_sparkdata.lazy.LazyCollectionAspectHandler;
import ru.aasmc.unsafe_sparkdata.lazy.LazySparkList;

@Configuration
public class StartConf {

    @Bean
    @Scope("prototype")
    public LazySparkList lazySparkList() {
        return new LazySparkList();
    }

    @Bean
    public FirstLevelCacheService firstLevelCacheService() {
        return new FirstLevelCacheService();
    }

    @Bean
    public LazyCollectionAspectHandler lazyCollectionAspectHandler() {
        return new LazyCollectionAspectHandler();
    }

}
