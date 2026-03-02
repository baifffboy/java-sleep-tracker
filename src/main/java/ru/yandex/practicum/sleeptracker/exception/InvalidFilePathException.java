package ru.yandex.practicum.sleeptracker.exception;

import java.io.IOException;

public class InvalidFilePathException extends IOException {
    public InvalidFilePathException(String message) {
        super(message);
    }
}
