package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.util.List;

public class AverageSessionInMinutes extends ForFunction {
    public AverageSessionInMinutes(String information) {
        super(information);
    }

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> listOfSleepSessions) {
        double average = listOfSleepSessions.stream()
                .mapToLong(session -> Duration.between(session.getBeginOfSleep(), session.getEndOfSleep()).toMinutes())
                .average()
                .orElse(0.0);
        return new SleepAnalysisResult(information, String.format("%f", average));
    }
}
