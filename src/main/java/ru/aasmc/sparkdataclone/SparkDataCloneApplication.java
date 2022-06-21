package ru.aasmc.sparkdataclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class SparkDataCloneApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SparkDataCloneApplication.class, args);
        CriminalRepo criminalRepo = context.getBean(CriminalRepo.class);
        criminalRepo.findByNumberGreaterThanOrderByNumber(15).forEach(System.out::println);
        List<Criminal> criminals = criminalRepo.findByNameContains("ova");
        Criminal criminal = criminals.get(0);
        System.out.println(criminal);
        List<Order> orders = criminal.getOrders();
        Order order = orders.get(0);
        System.out.println(order);
    }

}
