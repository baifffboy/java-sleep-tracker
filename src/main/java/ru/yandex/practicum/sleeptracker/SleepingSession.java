package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SleepingSession {
    private final LocalDateTime beginOfSleep;
    private final LocalDateTime endOfSleep;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime beginOfSleep, LocalDateTime endOfSleep, SleepQuality quality) {
        this.beginOfSleep = beginOfSleep;
        this.endOfSleep = endOfSleep;
        this.quality = quality;
    }

    public LocalDateTime getBeginOfSleep() {
        return beginOfSleep;
    }

    public LocalDateTime getEndOfSleep() {
        return endOfSleep;
    }

    public SleepQuality getQuality() {
        return quality;
    }
}
