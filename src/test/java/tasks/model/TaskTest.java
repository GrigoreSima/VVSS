package tasks.model;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;

    @Test
    void testTaskCreationValid() {
        try {
            task = new Task("Task1",
                    Task.getDateFormat().parse("2025-04-03 10:00"),
                    Task.getDateFormat().parse("2025-04-06 20:05"),
                    5
            );
            task.setActive(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertEquals("Task1", task.getTitle());
        assertEquals("2025-04-03 10:00", task.getFormattedDateStart());
        assertEquals("2025-04-06 20:05", task.getFormattedDateEnd());
        assertEquals(5, task.getRepeatInterval());
        assertTrue(task.isActive());

    }

    @Test
    void testTaskCreationInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            task = new Task("",
                    Task.getDateFormat().parse("2025-04-07 05:00"),
                    Task.getDateFormat().parse("2025-04-10 12:23"),
                    7
            );
            task.setActive(false);
        });

        String expectedMessage = "Title cannot be empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        exception = assertThrows(IllegalArgumentException.class, () -> {
            task = new Task("Task4",
                    Task.getDateFormat().parse("2025-04-15 10:00"),
                    Task.getDateFormat().parse("2025-04-20 20:05"),
                    -7
            );
            task.setActive(false);
        });

        expectedMessage = "interval should be >= 0";
        actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}