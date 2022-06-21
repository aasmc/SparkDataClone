package ru.aasmc.sparkdataclone;

import ru.aasmc.unsafe_sparkdata.SparkRepository;

import java.util.List;

public interface SpeakerRepo extends SparkRepository<Speaker> {

    List<Speaker> findByAgeBetween(int min, int max);
    long findByAgeGreaterThanCount(int min);
}