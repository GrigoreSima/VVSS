package tasks.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import tasks.model.ArrayTaskList;
import tasks.model.Task;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestAfisareTaskuriPerioada {

    static TasksService tasksService;
    static Task task, task2, task3, task4;

    @BeforeAll
    static void setUp() throws ParseException {
        task = new Task(
                "Task1",
                Task.getDateFormat().parse("2025-04-03 10:00"),
                Task.getDateFormat().parse("2025-04-06 20:05"),
                5
        );
        task.setActive(true);

        task2 = new Task(
                "Task2",
                Task.getDateFormat().parse("2015-04-07 10:00"),
                Task.getDateFormat().parse("2015-04-08 20:05"),
                5
        );
        task2.setActive(true);

        task3 = new Task(
                "Task3",
                Task.getDateFormat().parse("2025-04-09 10:00"),
                Task.getDateFormat().parse("2025-04-10 10:00"),
                0
        );
        task3.setActive(true);

        task4 = new Task(
                "Task4",
                Task.getDateFormat().parse("2025-04-13 10:00"),
                Task.getDateFormat().parse("2025-04-15 10:00"),
                0
        );
        task4.setActive(true);

        ArrayTaskList tasks = new ArrayTaskList();

        tasks.add(task); // F T
        tasks.add(task2); // T
        tasks.add(task3); // F F T
        tasks.add(task4); // F F F

        tasksService = new TasksService(tasks);
    }

    @Test
    @Order(1)
    void F02_TC01() throws ParseException {

        var result = (List<?>) tasksService.filterTasks(
                        Task.getDateFormat().parse("2025-04-03 00:00"),
                        Task.getDateFormat().parse("2025-04-06 23:59")
                );

        assertEquals(1, result.size());
        assertEquals(task, result.get(0));
    }

    @Test
    @Order(2)
    void F02_TC02() throws ParseException {

        var result = (List<?>) tasksService.filterTasks(
                Task.getDateFormat().parse("2025-04-07 00:00"),
                Task.getDateFormat().parse("2025-04-08 23:59")
        );

        assertEquals(0, result.size());
    }

    @Test
    @Order(3)
    void F02_TC03() throws ParseException {

        var result = (List<?>) tasksService.filterTasks(
                Task.getDateFormat().parse("2025-04-09 00:00"),
                Task.getDateFormat().parse("2025-04-09 10:00")
        );

        assertEquals(1, result.size());
        assertEquals(task3, result.get(0));
    }

    @Test
    @Order(4)
    void F02_TC04() throws ParseException {

        var result = (List<?>) tasksService.filterTasks(
                Task.getDateFormat().parse("2025-04-11 00:00"),
                Task.getDateFormat().parse("2025-04-12 23:59")
        );

        assertEquals(0, result.size());
    }

    @Test
    @Order(5)
    void F02_TC05() throws ParseException {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tasksService.filterTasks(
                    Task.getDateFormat().parse("2025-04-10 10:00"),
                    Task.getDateFormat().parse("2025-04-08 10:00")
            );
        });

        String expectedMessage = "Filter end date should be after start date";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}