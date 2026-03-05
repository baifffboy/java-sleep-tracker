package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class NumberOfSessionPerPeriod extends ForFunction {
    public NumberOfSessionPerPeriod(String information) {
        super(information);
    }

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> listOfSleepSessions) {
        return new SleepAnalysisResult(information, String.format("%d", listOfSleepSessions.size()));
    }
}
