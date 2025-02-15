package manager;

import data.Task;
import data.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    protected TaskManager taskManager;
    protected Task task1Test;
    protected Task task2Test;
    protected Task task3Test;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();

        task1Test = new Task("Task 1", "Description 1", TaskStatus.NEW, 0);
        task2Test = new Task("Task 2", "Description 2", TaskStatus.NEW, 1);
        task3Test = new Task("Task 3", "Description 3", TaskStatus.NEW, 2);
    }

    @Test
    void shouldAddTaskToHistory() {
        taskManager.addTask(task1Test);
        taskManager.addTask(task2Test);

        taskManager.getTaskById(task1Test.getId());
        taskManager.getTaskById(task2Test.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task1Test, history.get(0), "Первая задача в истории должна быть Task 1");
        assertEquals(task2Test, history.get(1), "Вторая задача в истории должна быть Task 2");
    }


    @Test
    void shouldRemoveTaskFromHistory() {
        taskManager.addTask(task1Test);
        taskManager.addTask(task2Test);

        taskManager.getTaskById(task1Test.getId());
        taskManager.getTaskById(task2Test.getId());

        taskManager.deleteTaskById(task1Test.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать 1 задачу после удаления");
        assertEquals(task2Test, history.get(0), "Оставшаяся задача должна быть Task 2");
    }

    @Test
    void shouldReturnEmptyHistoryAfterRemovingAllTasks() {
        taskManager.addTask(task1Test);
        taskManager.addTask(task2Test);

        taskManager.getTaskById(task1Test.getId());
        taskManager.getTaskById(task2Test.getId());

        taskManager.deleteTaskById(task1Test.getId());
        taskManager.deleteTaskById(task2Test.getId());

        List<Task> history = taskManager.getHistory();

        assertTrue(history.isEmpty(), "История должна быть пустой после удаления всех задач");
    }

    @Test
    void shouldNotFailWhenRemovingNonExistentTask() {
        taskManager.addTask(task1Test);
        taskManager.getTaskById(task1Test.getId());
        taskManager.deleteTaskById(999); // Удаляем несуществующую задачу

        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.size(), "История должна оставаться неизменной");
        assertEquals(task1Test, history.get(0), "Задача Task 1 должна остаться в истории");
    }

    @Test
    void shouldUpdateTaskPositionWhenReAdded() {
        taskManager.addTask(task1Test);
        taskManager.addTask(task2Test);

        taskManager.getTaskById(task1Test.getId());
        taskManager.getTaskById(task2Test.getId());
        taskManager.getTaskById(task1Test.getId()); // Повторный вызов

        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task2Test, history.get(0), "Task 2 должна остаться первой");
        assertEquals(task1Test, history.get(1), "Task 1 должна быть перемещена в конец");
    }
}
