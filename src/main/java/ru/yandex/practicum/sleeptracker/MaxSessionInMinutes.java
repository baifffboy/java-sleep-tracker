package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class MaxSessionInMinutes extends ForFunction {
    public MaxSessionInMinutes(String information) {
        super(information);
    }

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> listOfSleepSessions) {
        Optional<Long> minutes = listOfSleepSessions.stream()
                .map((session) -> Duration.between(session.getBeginOfSleep(), session.getEndOfSleep()).toMinutes())
                .max(Long::compareTo);
        return new SleepAnalysisResult(
                information,
                minutes.isPresent() ? String.format("%d", minutes.get()) : "-1"
        );
    }
}
