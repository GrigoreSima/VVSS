package tasks.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ArrayTaskListTest {


    private ArrayTaskList taskList;
    private Task mockTask1;
    private Task mockTask2;

    @BeforeEach
    public void setUp() {
        taskList = new ArrayTaskList();
        mockTask1 = mock(Task.class);
        mockTask2 = mock(Task.class);
    }

    @Test
    public void testAddTask() {
        assertEquals(0, taskList.size(), "Size should be 0");

        taskList.add(mockTask1);
        assertEquals(1, taskList.size(), "Size should be 1 after adding one tasks");

        taskList.add(mockTask2);
        assertEquals(2, taskList.size(), "Size should be 2 after adding two tasks");
    }

    @Test
    public void testGetTask() {
        taskList.add(mockTask1);
        taskList.add(mockTask2);

        assertEquals(mockTask1, taskList.getTask(0), "First task should match mockTask1");
        assertEquals(mockTask2, taskList.getTask(1), "Second task should match mockTask2");
    }

    @Test
    public void testGetTaskInvalidIndex() {
        taskList.add(mockTask1);

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(1));
    }

    @Test
    public void testAddNullTaskThrowsException() {
        assertThrows(NullPointerException.class, () -> taskList.add(null));
    }
}