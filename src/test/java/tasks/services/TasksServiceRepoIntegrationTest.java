package tasks.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.model.ArrayTaskList;
import tasks.model.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TasksServiceRepoIntegrationTest {
    private ArrayTaskList taskList;
    private TasksService tasksService;

    @BeforeEach
    public void setUp() {
        taskList = new ArrayTaskList();
        tasksService = new TasksService(taskList);
    }

    @Test
    public void testGetIntervalInHoursFormatsCorrectly() {
        Task task = mock(Task.class);
        when(task.getRepeatInterval()).thenReturn(3660); // 1 hour, 1 minute

        String result = tasksService.getIntervalInHours(task);
        assertEquals("01:01", result);
    }


    @Test
    public void testFilterTasksReturnsExpectedTasks() {
        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);

        Date now = new Date();
        Date inFuture = new Date(now.getTime() + 100000);

        when(task1.nextTimeAfter(now)).thenReturn(new Date(now.getTime() + 50000));
        when(task2.nextTimeAfter(now)).thenReturn(new Date(now.getTime() + 150000));
        when(task1.getTitle()).thenReturn("Task 1");
        when(task2.getTitle()).thenReturn("Task 2");

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