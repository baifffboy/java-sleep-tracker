package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeOfPerson extends ForFunction {
    public TypeOfPerson(String information) {
        super(information);
    }

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> listOfSleepSessions) {
        Set<LocalDate> setOfOwl = listOfSleepSessions.stream()
                //клиент сова
                .filter(session ->
                        (session.getBeginOfSleep().isAfter(LocalDateTime.of(session.getBeginOfSleep().toLocalDate().plusDays(1), LocalTime.of(23, 0)))
                                && session.getEndOfSleep().isAfter(LocalDateTime.of(session.getEndOfSleep().toLocalDate(), LocalTime.of(9, 0))))
                )
                .map(session -> {
                    if (session.getBeginOfSleep().toLocalTime().isAfter(LocalTime.of(12, 0))) {
                        return session.getBeginOfSleep().toLocalDate().plusDays(1);
                    }
                    return session.getBeginOfSleep().toLocalDate();
                })
                .collect(Collectors.toSet());

        Set<LocalDate> setOfLark = listOfSleepSessions.stream()
                //клиент жаворонок
                .filter(session ->
                        (session.getBeginOfSleep().isBefore(LocalDateTime.of(session.getBeginOfSleep().toLocalDate().plusDays(1), LocalTime.of(22, 0)))
                                && session.getEndOfSleep().isBefore(LocalDateTime.of(session.getEndOfSleep().toLocalDate(), LocalTime.of(7, 0))))
                )
                .map(session -> {
                    if (session.getBeginOfSleep().toLocalTime().isAfter(LocalTime.of(12, 0))) {
                        return session.getBeginOfSleep().toLocalDate().plusDays(1);
                    }
                    return session.getBeginOfSleep().toLocalDate();
                })
                .collect(Collectors.toSet());

        Set<LocalDate> setOfPigeon = listOfSleepSessions.stream()
                //клиент голубь
                .filter(session -> {
                            if (session.getBeginOfSleep().toLocalTime().isAfter(LocalTime.of(12, 0))) {
                                return !setOfOwl.contains(session.getBeginOfSleep().toLocalDate().plusDays(1))
                                        && !setOfLark.contains(session.getBeginOfSleep().toLocalDate().plusDays(1))
                                        && session.getBeginOfSleep().toLocalTime().isAfter(LocalTime.of(21, 0))
                                        && session.getEndOfSleep().toLocalTime().isBefore(LocalTime.of(10, 0));
                            }
                            return !setOfOwl.contains(session.getBeginOfSleep().toLocalDate())
                                    && !setOfLark.contains(session.getBeginOfSleep().toLocalDate())
                                    && session.getBeginOfSleep().toLocalTime().isAfter(LocalTime.of(21, 0))
                                    && session.getEndOfSleep().toLocalTime().isBefore(LocalTime.of(10, 0));
                        }
                )
                .map(session -> {
                    if (session.getBeginOfSleep().toLocalTime().isAfter(LocalTime.of(12, 0))) {
                        return session.getBeginOfSleep().toLocalDate().plusDays(1);
                    }
                    return session.getBeginOfSleep().toLocalDate();
                })
                .collect(Collectors.toSet());

        final String type;
        if ((setOfOwl.size() < setOfPigeon.size() && setOfLark.size() < setOfPigeon.size()) || (setOfOwl.size() == setOfLark.size()))
            type = "Голубь";
        else if (setOfOwl.size() > setOfPigeon.size() && setOfLark.size() < setOfOwl.size()) type = "Сова";
        else type = "Жаворонок";
        return new SleepAnalysisResult(information, String.format("%s", type));
    }
}
