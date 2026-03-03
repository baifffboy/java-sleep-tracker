package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.sleeptracker.exception.InvalidFilePathException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SleepTrackerAppTest {

    @TempDir
    Path tempDir;

    private Path createTestFile(String... lines) throws IOException {
        Path testFile = tempDir.resolve("test_sleep_log.txt");
        Files.write(testFile, List.of(lines));
        return testFile;
    }

    @Test
    void testFileLoading_NormalFile() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:15;02.10.25 07:30;GOOD",
                "02.10.25 23:50;03.10.25 06:40;NORMAL"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testFileLoading_EmptyFile() throws IOException {
        Path testFile = createTestFile();
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testFileLoading_FileNotFound() {
        String[] args = {"nonexistent.txt"};
        Exception exception = assertThrows(RuntimeException.class,
                () -> SleepTrackerApp.main(args));
        assertTrue(exception.getCause() instanceof InvalidFilePathException);
    }

    @Test
    void testFileLoading_NoArgument() {
        String[] args = {};
        Exception exception = assertThrows(RuntimeException.class,
                () -> SleepTrackerApp.main(args));
        assertTrue(exception.getCause() instanceof InvalidFilePathException);
        assertEquals("В командной строке не указан путь к файлу",
                exception.getCause().getMessage());
    }

    // ============= ТЕСТЫ С РАЗНЫМИ ДАННЫМИ =============
    @Test
    void testWithNormalData() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:15;02.10.25 07:30;GOOD",
                "02.10.25 23:50;03.10.25 06:40;NORMAL",
                "03.10.25 14:10;03.10.25 15:00;BAD",
                "03.10.25 23:40;04.10.25 08:00;BAD"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithSingleSession() throws IOException {
        Path testFile = createTestFile("01.10.25 23:15;02.10.25 07:30;GOOD");
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithAllGoodQualities() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:15;02.10.25 07:30;GOOD",
                "02.10.25 23:50;03.10.25 06:40;GOOD",
                "03.10.25 23:40;04.10.25 08:00;GOOD"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithAllBadQualities() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:15;02.10.25 07:30;BAD",
                "02.10.25 23:50;03.10.25 06:40;BAD",
                "03.10.25 23:40;04.10.25 08:00;BAD"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithAllNormalQualities() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:15;02.10.25 07:30;NORMAL",
                "02.10.25 23:50;03.10.25 06:40;NORMAL",
                "03.10.25 23:40;04.10.25 08:00;NORMAL"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithShortSessions() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 14:00;01.10.25 14:30;NORMAL",  // 30 мин
                "01.10.25 15:00;01.10.25 15:20;NORMAL",  // 20 мин
                "01.10.25 23:15;02.10.25 00:15;BAD"      // 60 мин
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithLongSessions() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 22:00;02.10.25 10:00;GOOD",    // 12 часов
                "02.10.25 23:00;03.10.25 11:00;GOOD",    // 12 часов
                "03.10.25 21:00;04.10.25 09:00;GOOD"     // 12 часов
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithSessionsCrossingMidnight() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:30;02.10.25 06:30;GOOD",
                "02.10.25 23:45;03.10.25 05:45;NORMAL",
                "03.10.25 23:50;04.10.25 07:50;BAD"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithDaySessionsOnly() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 13:00;01.10.25 14:30;NORMAL",
                "01.10.25 15:00;01.10.25 16:00;NORMAL",
                "02.10.25 12:30;02.10.25 13:45;NORMAL"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithNightSessionsOnly() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:15;02.10.25 07:30;GOOD",
                "02.10.25 23:50;03.10.25 06:40;GOOD",
                "03.10.25 23:40;04.10.25 08:00;GOOD"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithMixedQualities() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:15;02.10.25 07:30;GOOD",
                "02.10.25 23:50;03.10.25 06:40;NORMAL",
                "03.10.25 14:10;03.10.25 15:00;BAD",
                "03.10.25 23:40;04.10.25 08:00;BAD",
                "04.10.25 23:15;05.10.25 07:30;GOOD",
                "05.10.25 13:30;05.10.25 14:15;NORMAL"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithGapInDates() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:15;02.10.25 07:30;GOOD",
                "05.10.25 23:50;06.10.25 06:40;NORMAL",
                "10.10.25 23:40;11.10.25 08:00;BAD"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithSessionsStartingAfterNoon() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 13:15;01.10.25 14:30;NORMAL",
                "02.10.25 14:50;02.10.25 16:40;NORMAL",
                "03.10.25 15:10;03.10.25 16:00;NORMAL"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithSessionsStartingBeforeNoon() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 09:15;01.10.25 10:30;NORMAL",
                "02.10.25 08:50;02.10.25 09:40;NORMAL",
                "03.10.25 10:10;03.10.25 11:00;NORMAL"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithOwlPattern() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 23:30;02.10.25 09:30;GOOD",
                "02.10.25 23:45;03.10.25 09:15;GOOD",
                "03.10.25 23:50;04.10.25 09:45;GOOD"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithLarkPattern() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 21:30;02.10.25 06:30;GOOD",
                "02.10.25 21:45;03.10.25 06:45;GOOD",
                "03.10.25 21:15;04.10.25 06:15;GOOD"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testWithPigeonPattern() throws IOException {
        Path testFile = createTestFile(
                "01.10.25 22:30;02.10.25 07:30;GOOD",
                "02.10.25 22:45;03.10.25 07:45;GOOD",
                "03.10.25 22:15;04.10.25 07:15;GOOD"
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    // ============= EDGE CASES =============
    @Test
    void testEdgeCase_InvalidFileFormat() throws IOException {
        Path testFile = createTestFile("invalid line without semicolons");
        String[] args = {testFile.toString()};
        assertThrows(Exception.class, () -> SleepTrackerApp.main(args));
    }

    @Test
    void testEdgeCase_InvalidQuality() throws IOException {
        Path testFile = createTestFile("01.10.25 23:15;02.10.25 07:30;INVALID");
        String[] args = {testFile.toString()};
        assertThrows(Exception.class, () -> SleepTrackerApp.main(args));
    }

    @Test
    void testEdgeCase_InvalidDateFormat() throws IOException {
        Path testFile = createTestFile("2025-10-01 23:15;2025-10-02 07:30;GOOD");
        String[] args = {testFile.toString()};
        assertThrows(Exception.class, () -> SleepTrackerApp.main(args));
    }

    @Test
    void testEdgeCase_EmptyLines() throws IOException {
        Path testFile = createTestFile(
                "",
                "01.10.25 23:15;02.10.25 07:30;GOOD",
                "",
                "02.10.25 23:50;03.10.25 06:40;NORMAL",
                ""
        );
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }

    @Test
    void testEdgeCase_LargeFile() throws IOException {
        String[] lines = new String[100];
        for (int i = 0; i < 100; i++) {
            String date = String.format("%02d", i % 30 + 1);
            lines[i] = date + ".10.25 23:15;" + date + ".10.25 07:30;GOOD";
        }
        Path testFile = createTestFile(lines);
        String[] args = {testFile.toString()};
        assertDoesNotThrow(() -> SleepTrackerApp.main(args));
    }
}