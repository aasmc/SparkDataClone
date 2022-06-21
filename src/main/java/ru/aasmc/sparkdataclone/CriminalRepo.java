package ru.aasmc.sparkdataclone;

import ru.aasmc.unsafe_sparkdata.SparkRepository;

import java.util.List;

public interface CriminalRepo extends SparkRepository<Criminal> {
    List<Criminal> findByNumberGreaterThanOrderByNumber(int min);

    List<Criminal> findByNameContains(String s);
}
