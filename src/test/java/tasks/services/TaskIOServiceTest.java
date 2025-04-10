package tasks.services;

import org.junit.jupiter.api.Test;
import tasks.model.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TaskIOServiceTest {
    private Task task;

    @Test
    void addTaskValidECP() {
        TaskIOService.setTaskList(new ArrayList<>());

        try {
            task = new Task("Task1",
                    Task.getDateFormat().parse("2025-04-03 10:00"),
                    Task.getDateFormat().parse("2025-04-06 20:05"),
                    5
            );
            task.setActive(true);
            TaskIOService.add(task);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Task lastTask = null;
        try {
            lastTask = TaskIOService.getLast();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lastTask != null) {
            assertEquals("Task1", lastTask.getTitle());
            assertEquals("2025-04-03 10:00", lastTask.getFormattedDateStart());
            assertEquals("2025-04-06 20:05", lastTask.getFormattedDateEnd());
            assertEquals(5, lastTask.getRepeatInterval());
            assertTrue(lastTask.isActive());
        }
    }

    @Test
    void addTaskInvalidECP() {
        TaskIOService.setTaskList(new ArrayList<>());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            task = new Task("",
                    Task.getDateFormat().parse("2025-04-07 05:00"),
                    Task.getDateFormat().parse("2025-04-10 12:23"),
                    7
            );
            task.setActive(false);
            TaskIOService.add(task);
        });

        String expectedMessage = "Title cannot be empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));


        exception = assertThrows(IllegalArgumentException.class, () -> {
            task = new Task("Task2",
                    Task.getDateFormat().parse("2025-04-15 07:00"),
                    Task.getDateFormat().parse("2025-04-20 13:05"),
                    -6
            );
            task.setActive(false);
            TaskIOService.add(task);
        });

        expectedMessage = "interval should be > 0";
        actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void addTaskValidBVA() {
        TaskIOService.setTaskList(new ArrayList<>());

        try {
            task = new Task("Task1",
                    Task.getDateFormat().parse("2025-04-03 10:00"),
                    Task.getDateFormat().parse("2025-04-06 20:05"),
                    5
            );
            task.setActive(true);
            TaskIOService.add(task);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Task lastTask = null;
        try {
            lastTask = TaskIOService.getLast();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lastTask != null) {
            assertEquals("Task1", lastTask.getTitle());
            assertEquals("2025-04-03 10:00", lastTask.getFormattedDateStart());
            assertEquals("2025-04-06 20:05", lastTask.getFormattedDateEnd());
            assertEquals(5, lastTask.getRepeatInterval());
            assertTrue(lastTask.isActive());
        }

        try {
            task = new Task("Task6",
                    Task.getDateFormat().parse("2050-04-15 10:00"),
                    Task.getDateFormat().parse("2025-04-20 20:05"),
                    3
            );
            task.setActive(false);
            TaskIOService.add(task);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            lastTask = TaskIOService.getLast();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lastTask != null) {
            assertEquals("Task6", lastTask.getTitle());
            assertEquals("2050-04-15 10:00", lastTask.getFormattedDateStart());
            assertEquals("2025-04-20 20:05", lastTask.getFormattedDateEnd());
            assertEquals(3, lastTask.getRepeatInterval());
            assertFalse(lastTask.isActive());
        }
    }

    @Test
    void addTaskInvalidBVA() {
        TaskIOService.setTaskList(new ArrayList<>());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            task = new Task("Task2",
                    Task.getDateFormat().parse("2051-04-15 07:00"),
                    Task.getDateFormat().parse("2025-04-20 13:05"),
                    2
            );
            task.setActive(false);
            TaskIOService.add(task);
        });

        String expectedMessage = "Start date cannot be > 2050";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));


        exception = assertThrows(IllegalArgumentException.class, () -> {
            task = new Task("Task3",
                    Task.getDateFormat().parse("1979-04-10 13:05"),
                    Task.getDateFormat().parse("2025-04-10 13:05"),
                    7
            );
            task.setActive(false);
            TaskIOService.add(task);
        });

        expectedMessage = "Start date cannot be < 1980";
        actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}