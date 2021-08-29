package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );


        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

     //  System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        //подсчет калорий за день
        Map<LocalDate, Integer> counting = new HashMap<>();
        for (UserMeal meal: meals) {
            LocalDate dayMeal = meal.getDateTime().toLocalDate();
            Integer calories = counting.get(dayMeal);
            if (calories != null){
                counting.put(dayMeal, calories + meal.getCalories());

            }
            else {
                counting.put(dayMeal,meal.getCalories());
            }
            //проверка на true false
        }
        List<UserMealWithExcess> main_res = new ArrayList<>();
        for (UserMeal newMeal: meals) {
            Integer calories = counting.get(newMeal.getDate());
            boolean excess;
            if (calories > caloriesPerDay){
                excess = false;
            }
            else {
                excess = true;
            }
            UserMealWithExcess end = new UserMealWithExcess(newMeal, excess);
            main_res.add(end);

        }
        //добавление фильтра между времени endT и startT
        List<UserMealWithExcess> MAIN_RESULT = new ArrayList<>();

        for (UserMealWithExcess userExcess : main_res) {
            LocalTime currentTime = userExcess.getLocalDate().atStartOfDay().toLocalTime();
            if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                MAIN_RESULT.add(userExcess);
            }
        }

        return MAIN_RESULT;
    }
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        Map<LocalDate, Integer> maps = meals.stream().collect(Collectors.groupingBy(UserMeal::getDate,
            Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream().filter(s -> TimeUtil.isBetweenHalfOpen(s.getDateTime().toLocalTime(),startTime,endTime))
            .map(s-> new UserMealWithExcess(s.getDateTime(),s.getDescription(),s.getCalories(),
                maps.get(s.getDate())
                >caloriesPerDay)).collect(Collectors.toList());

    }
}
