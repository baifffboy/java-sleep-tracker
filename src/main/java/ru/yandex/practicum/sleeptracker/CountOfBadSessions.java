package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class CountOfBadSessions extends ForFunction {
    public CountOfBadSessions(String information) {
        super(information);
    }

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> listOfSleepSessions) {
        long count = listOfSleepSessions.stream()
                .filter(session -> session.getQuality().equals(SleepQuality.BAD))
                .count();
        return new SleepAnalysisResult(information, String.format("%d", count));
    }
}
