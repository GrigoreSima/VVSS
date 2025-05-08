package tasks.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.model.ArrayTaskList;
import tasks.model.Task;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tasks.services.TestAfisareTaskuriPerioada.task;

class TasksServiceTaskIntegrationTest {
    private ArrayTaskList taskList;
    private TasksService tasksService;

    @BeforeEach
    public void setUp() {
        taskList = new ArrayTaskList();
        tasksService = new TasksService(taskList);
    }

    @Test
    public void testGetIntervalInHoursFormatsCorrectly() throws ParseException {
        Task task = new Task("Task1",
                Task.getDateFormat().parse("2025-04-03 10:00"),
                Task.getDateFormat().parse("2025-04-06 20:05"),
                10000
        );
        task.setActive(true);

        String result = tasksService.getIntervalInHours(task);
        assertEquals("02:46", result);
    }


    @Test
    public void testFilterTasksReturnsExpectedTasks() {
        Date now = new Date();
        Date inFuture = new Date(now.getTime() + 100000);

        Task task1 = new Task("Task1",
                new Date(now.getTime()),
                new Date(now.getTime() + 1000),
                1
        );
        task1.setActive(true);

        Task task2 = new Task("Task4",
                new Date(now.getTime()),
                new Date(now.getTime() + 150000),
                5000
        );
        task2.setActive(false);

        taskList.add(task1);
        taskList.add(task2);

        Iterable<Task> result = tasksService.filterTasks(now, inFuture);

        List<Task> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        assertEquals(1, resultList.size());
        assertTrue(resultList.contains(task1));
        assertFalse(resultList.contains(task2));
    }

    @Test
    public void testFilterTasksThrowsOnInvalidDateRange() {
        Date end = new Date();
        Date start = new Date(end.getTime() + 10000);

        assertThrows(IllegalArgumentException.class, () -> tasksService.filterTasks(start, end));
    }
}