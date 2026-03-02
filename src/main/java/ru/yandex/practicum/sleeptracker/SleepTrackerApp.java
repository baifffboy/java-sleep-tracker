package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.exception.InvalidFilePathException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SleepTrackerApp {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private List<Function<List<SleepingSession>, String>> listOfFunctions = new ArrayList<>();
    private List<SleepingSession> listOfSleepSessions = new ArrayList<>();

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
                    return "Данная функция открывает файл и считывает его данные в List класса лога часов";
                }
        );

        sleepTracker.listOfFunctions.stream()
                .forEach(function ->
                    System.out.println(function.apply(sleepTracker.listOfSleepSessions)));
    }

    public void addFunction(Function<List<SleepingSession>, String> function){
        listOfFunctions.add(function);
    }
}