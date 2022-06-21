package ru.aasmc.sparkdataclone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.aasmc.unsafe_sparkdata.util.ForeignKey;
import ru.aasmc.unsafe_sparkdata.util.Source;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Source("data/criminals.csv")
public class Criminal {
    private long id;
    private String name;
    private int number;

    @ForeignKey("criminalId")
    private List<Order> orders;


    public void printAllOrders() {
        orders.forEach(System.out::println);
    }

}
