package com.company.onboarding.report;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList(
                "apple", "banana", "cherry", "date", "elderberry", "fig"
        );

        // Задача: получить список длинных слов (длина > 5), в верхнем регистре, отсортированных
        List<String> longWords = words.stream()
                .filter(word -> word.length() > 4)      // фильтруем
                .map(String::toUpperCase)                  // преобразуем
                .sorted()                                 // сортируем
                .collect(Collectors.toList());          // собираем в список

        System.out.println(longWords);
        // Вывод: [APPLE, BANANA, CHERRY, ELDERBERRY]
    }
}
