package ru.yandex.practicum.sleeptracker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class SleepTrackerApp {
    private final List<Function<List<SleepingSession>, SleepAnalysisResult>> listOfFunctions = new LinkedList<>();
    private final List<SleepingSession> listOfSleepSessions = new ArrayList<>();
    private final List<SleepAnalysisResult> listOfResultAnalysisSleep = new LinkedList<>();

    public static void main(String[] args) {
        SleepTrackerApp sleepTracker = new SleepTrackerApp();

        Function<List<SleepingSession>, SleepAnalysisResult> f1 = new FileOpenAndUpload("Данная функция открывает файл и считывает логи часов в listOfSleepSessions", "src/main/resources/sleep_log.txt");
        sleepTracker.addFunction(f1::apply);

        Function<List<SleepingSession>, SleepAnalysisResult> f2 = new NumberOfSessionPerPeriod("Данная функция отображает количество сессий представленных за период");
        sleepTracker.addFunction(f2::apply);

        Function<List<SleepingSession>, SleepAnalysisResult> f3 = new MinSessionInMinutes("Данная функция отображает минимальную продолжительность сессии (в минутах)");
        sleepTracker.addFunction(f3::apply);

        Function<List<SleepingSession>, SleepAnalysisResult> f4 = new MaxSessionInMinutes("Данная функция отображает максимальную продолжительность сессии (в минутах)");
        sleepTracker.addFunction(f4::apply);

        Function<List<SleepingSession>, SleepAnalysisResult> f5 = new AverageSessionInMinutes("Данная функция отображает среднюю продолжительность сессии (в минутах)");
        sleepTracker.addFunction(f5::apply);

        Function<List<SleepingSession>, SleepAnalysisResult> f6 = new CountOfBadSessions("Данная функция отображает количество плохих сессий");
        sleepTracker.addFunction(f6::apply);

        Function<List<SleepingSession>, SleepAnalysisResult> f7 = new CountOfSleeplessNights("Данная функция отображает количество бессонных ночей");
        sleepTracker.addFunction(f7::apply);

        Function<List<SleepingSession>, SleepAnalysisResult> f8 = new TypeOfPerson("Данная функция отображает тип человека по его привычке сна (голубь, сова или жаворонок)");
        sleepTracker.addFunction(f8::apply);

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

    public List<Function<List<SleepingSession>, SleepAnalysisResult>> getListOfFunctions() {
        return listOfFunctions;
    }
}