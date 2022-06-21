package ru.aasmc.sparkdataclone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.aasmc.unsafe_sparkdata.util.Source;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Source("data/orders.csv")
public class Order {
    //name,desc,price,criminalId
    private String name;
    private String desc;
    private int price;

    private long criminalId;


}
