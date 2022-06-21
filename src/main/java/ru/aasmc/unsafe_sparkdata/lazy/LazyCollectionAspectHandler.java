package ru.aasmc.unsafe_sparkdata.lazy;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@Aspect
public class LazyCollectionAspectHandler {

    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private FirstLevelCacheService cacheService;

    @Before("execution(* ru.aasmc.unsafe_sparkdata.lazy.LazySparkList.*(..)) && " +
            "execution( * java.util.List.*(..))"
    )
    public void setLazyCollections(JoinPoint jp) {
        LazySparkList lazyList = (LazySparkList) jp.getTarget();
        if (!lazyList.initialized()) {
            List<Object> content = cacheService.readDataFor(lazyList.getOwnerId(),
                    lazyList.getModelClass(),
                    lazyList.getPathToSource(),
                    lazyList.getForeignKeyName(),
                    context);
            lazyList.setContent(content);
        }
    }

}
