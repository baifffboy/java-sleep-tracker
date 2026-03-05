package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public abstract class ForFunction implements Function<List<SleepingSession>, SleepAnalysisResult> {
    final String information;

    public ForFunction(String information) {
        this.information = information;
    }
}
