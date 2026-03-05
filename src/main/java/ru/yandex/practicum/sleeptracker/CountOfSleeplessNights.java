package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class CountOfSleeplessNights extends ForFunction {
    public CountOfSleeplessNights(String information) {
        super(information);
    }

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> listOfSleepSessions) {
        if (listOfSleepSessions.isEmpty()) {
            return new SleepAnalysisResult(
                    information,
                    "0"
            );
        }

        LocalDateTime firstLogBeginOfSleep = listOfSleepSessions.getFirst().getBeginOfSleep();
        LocalDate firstNight = firstLogBeginOfSleep.toLocalTime().isAfter(LocalTime.of(12, 0))
                ? firstLogBeginOfSleep.toLocalDate().plusDays(1)
                : firstLogBeginOfSleep.toLocalDate();

        LocalDateTime lastLogEndOfSleep = listOfSleepSessions.getLast().getEndOfSleep();
        LocalDate lastNight = lastLogEndOfSleep.toLocalTime().isAfter(LocalTime.of(12, 0))
                ? lastLogEndOfSleep.toLocalDate().plusDays(1)
                : lastLogEndOfSleep.toLocalDate();

        long days = ChronoUnit.DAYS.between(firstNight, lastNight) + 1;
        long countOfNightSleep = listOfSleepSessions.stream()
                // сейчас у меня будут записи когда клиент спал с 00.00 до 6.00
                .filter(session ->
                        (session.getBeginOfSleep().isBefore(LocalDateTime.of(session.getBeginOfSleep().toLocalDate().plusDays(1), LocalTime.of(0, 0)))
                                && session.getEndOfSleep().isAfter(LocalDateTime.of(session.getEndOfSleep().toLocalDate(), LocalTime.of(6, 0))))
                                || (session.getBeginOfSleep().isAfter(LocalDateTime.of(session.getBeginOfSleep().toLocalDate(), LocalTime.of(0, 0)))
                                && session.getEndOfSleep().isBefore(LocalDateTime.of(session.getEndOfSleep().toLocalDate(), LocalTime.of(6, 0))))
                )
                .map(session -> {
                    if (session.getBeginOfSleep().toLocalTime().isAfter(LocalTime.of(12, 0))) {
                        return session.getBeginOfSleep().toLocalDate().plusDays(1);
                    }
                    return session.getBeginOfSleep().toLocalDate();
                })
                .collect(Collectors.toSet())
                .size();
        return new SleepAnalysisResult(information, String.format("%d", days - countOfNightSleep));
    }
}
