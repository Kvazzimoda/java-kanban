package manager;

import data.Task;
import data.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 9, 0));
        task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 10, 0));
        task3 = new Task("Task 3", "Desc 3", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 11, 0));
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
    }

    @Test
    void testAddTaskToHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "History should contain one task");
        assertEquals(task1, history.get(0), "Task in history should match the added task");
    }

    @Test
    void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "History should be empty initially");
    }

    @Test
    void testDuplicateTasksInHistory() {
        historyManager.add(task1);
        historyManager.add(task1); // Дубликат
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "History should contain only one instance of the task");
        assertEquals(task1, history.get(0), "Task in history should match the added task");
    }

    @Test
    void testRemoveFromHistoryBeginning() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId()); // Удаляем первый элемент
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "History should contain two tasks");
        assertEquals(task2, history.get(0), "First task should be task2");
        assertEquals(task3, history.get(1), "Second task should be task3");
    }

    @Test
    void testRemoveFromHistoryMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId()); // Удаляем средний элемент
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "History should contain two tasks");
        assertEquals(task1, history.get(0), "First task should be task1");
        assertEquals(task3, history.get(1), "Second task should be task3");
    }

    @Test
    void testRemoveFromHistoryEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId()); // Удаляем последний элемент
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "History should contain two tasks");
        assertEquals(task1, history.get(0), "First task should be task1");
        assertEquals(task2, history.get(1), "Second task should be task2");
    }
}
