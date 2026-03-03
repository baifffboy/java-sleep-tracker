package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.exception.InvalidFilePathException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private final List<Function<List<SleepingSession>, SleepAnalysisResult>> listOfFunctions = new LinkedList<>();
    private final List<SleepingSession> listOfSleepSessions = new ArrayList<>();
    private final List<SleepAnalysisResult> listOfResultAnalysisSleep = new LinkedList<>();

    public static void main(String[] args) {
        SleepTrackerApp sleepTracker = new SleepTrackerApp();
        sleepTracker.addFunction(
                (listOfSleepSessions) -> {
                    if (args.length == 0) {
                        throw new RuntimeException(new InvalidFilePathException("В командной строке не указан путь к файлу"));
                    }
                    String filepath = args[0];
                    try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
                        String line;
                        while (reader.ready()) {
                            line = reader.readLine();
                            if (line.trim().isEmpty()) continue;
                            String[] splitString = line.split(";");
                            LocalDateTime beginOfSleep = LocalDateTime.parse(splitString[0], FORMATTER);
                            LocalDateTime endOfSleep = LocalDateTime.parse(splitString[1], FORMATTER);
                            listOfSleepSessions.add(
                                    new SleepingSession(beginOfSleep, endOfSleep, SleepQuality.valueOf(splitString[2]))
                            );
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(new InvalidFilePathException("Не удалось открыть файл: " + filepath + " - " + e.getMessage()));
                    }
                    return new SleepAnalysisResult("Данная функция открывает файл и считывает его данные в List класса лога часов", "");
                }
        );

        sleepTracker.addFunction(
                (listOfSleepSessions) ->
                        new SleepAnalysisResult("Данная функция отображает количество сессий представлено за период", String.format("%d", listOfSleepSessions.size()))
        );

        sleepTracker.addFunction(
                (listOfSleepSessions) -> {
                    Optional<Long> minutes = listOfSleepSessions.stream()
                            .map((session) -> Duration.between(session.getBeginOfSleep(), session.getEndOfSleep()).toMinutes())
                            .min(Long::compareTo);
                    return new SleepAnalysisResult(
                            "Данная функция отображает минимальную продолжительность сессии (в минутах)",
                            minutes.isPresent() ? String.format("%d", minutes.get()) : "-1"
                    );
                }
        );

        sleepTracker.addFunction(
                (listOfSleepSessions) -> {
                    Optional<Long> minutes = listOfSleepSessions.stream()
                            .map((session) -> Duration.between(session.getBeginOfSleep(), session.getEndOfSleep()).toMinutes())
                            .max(Long::compareTo);
                    return new SleepAnalysisResult(
                            "Данная функция отображает максимальную продолжительность сессии (в минутах)",
                            minutes.isPresent() ? String.format("%d", minutes.get()) : "-1"
                    );
                }
        );

        sleepTracker.addFunction(
                (listOfSleepSessions) -> {
                    double average = listOfSleepSessions.stream()
                            .mapToLong(session -> Duration.between(session.getBeginOfSleep(), session.getEndOfSleep()).toMinutes())
                            .average()
                            .orElse(0.0);
                    return new SleepAnalysisResult("Данная функция отображает среднюю продолжительность сессии (в минутах)", String.format("%f", average));
                }
        );

        sleepTracker.addFunction(
                (listOfSleepSessions) -> {
                    long count = listOfSleepSessions.stream()
                            .filter(session -> session.getQuality().equals(SleepQuality.BAD))
                            .count();
                    return new SleepAnalysisResult("Данная функция отображает количество плохих сессий", String.format("%d", count));
                }
        );

        sleepTracker.addFunction(
                (listOfSleepSessions) -> {
                    if (listOfSleepSessions.isEmpty()) {
                        return new SleepAnalysisResult(
                                "Данная функция отображает количество бессонных ночей",
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
                    return new SleepAnalysisResult("Данная функция отображает количество бессонных ночей", String.format("%d", days - countOfNightSleep));
                }
        );

        sleepTracker.addFunction(
                (listOfSleepSessions) -> {
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
                    return new SleepAnalysisResult("Данная функция отображает тип человека по его привычке сна (голубь, сова или жаворонок)", String.format("%s", type));
                }
        );

        sleepTracker.listOfFunctions.stream()
                .forEach(function ->
                        sleepTracker.listOfResultAnalysisSleep.add(function.apply(sleepTracker.listOfSleepSessions)));

        sleepTracker.listOfResultAnalysisSleep.stream()
                .forEach(function -> {
                            if (function.getValue().isBlank())
                                System.out.printf("Описание функции:\n\"%s\"\n\n", function.getDescription());
                            else
                                System.out.printf("Описание функции:\n\"%s\"\nЗначение:\n%s\n\n", function.getDescription(), function.getValue());
                        }
                );

    }

    public void addFunction(Function<List<SleepingSession>, SleepAnalysisResult> function) {
        listOfFunctions.add(function);
    }
}