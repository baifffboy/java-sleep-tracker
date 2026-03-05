package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.exception.InvalidFilePathException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileOpenAndUpload extends ForFunction {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private final String filepath;

    public FileOpenAndUpload(String information, String filepath) {
        super(information);
        this.filepath = filepath;
    }

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> listOfSleepSessions) {
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
        return new SleepAnalysisResult(information, "");
    }
}
