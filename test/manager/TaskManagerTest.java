package manager;

import data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected SubTask subTask;

    // Абстрактный метод для инициализации taskManager в подклассах
    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
        task = new Task("Task 1", "Description 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 9, 0));
        epic = new Epic("Epic 1", "Epic Description");
        subTask = new SubTask("SubTask 1", "SubTask Description", TaskStatus.NEW, 0,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));
    }

    @Test
    void testAddTask() {
        taskManager.addTask(task);
        Optional<Task> retrievedTask = taskManager.getTaskById(task.getId());
        assertTrue(retrievedTask.isPresent(), "Task should be added");
        assertEquals(task, retrievedTask.get(), "Retrieved task should match the added task");
    }

    @Test
    void testAddEpic() {
        taskManager.addEpic(epic);
        Optional<Epic> retrievedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(retrievedEpic.isPresent(), "Epic should be added");
        assertEquals(epic, retrievedEpic.get(), "Retrieved epic should match the added epic");
    }

    @Test
    void testAddSubtask() {
        taskManager.addEpic(epic);
        subTask.setEpicId(epic.getId());
        taskManager.addSubtask(subTask);
        Optional<SubTask> retrievedSubTask = taskManager.getSubTaskById(subTask.getId());
        assertTrue(retrievedSubTask.isPresent(), "SubTask should be added");
        assertEquals(subTask, retrievedSubTask.get(), "Retrieved subtask should match the added subtask");
        assertEquals(epic.getId(), retrievedSubTask.get().getEpicId(), "SubTask should be linked to the correct Epic");
    }

    @Test
    void testUpdateTask() {
        taskManager.addTask(task);
        Task updatedTask = new Task("Updated Task", "Updated Description", TaskStatus.DONE,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 9, 0));
        updatedTask.setId(task.getId());
        taskManager.updateTask(task, updatedTask);
        Optional<Task> retrievedTask = taskManager.getTaskById(task.getId());
        assertTrue(retrievedTask.isPresent(), "Задача должна быть обновлена");
        assertEquals(updatedTask, retrievedTask.get(), "Задача должна быть обновлена новыми значениями");
    }

    @Test
    void testUpdateEpic() {
        taskManager.addEpic(epic);
        Epic updatedEpic = new Epic("Updated Epic", "Updated Description");
        updatedEpic.setId(epic.getId());
        taskManager.updateEpic(epic, "Updated Epic", "Updated Description");
        Optional<Epic> retrievedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(retrievedEpic.isPresent(), "Epic should be updated");
        assertEquals(updatedEpic.getTitle(), retrievedEpic.get().getTitle(), "Epic title should be updated");
    }

    @Test
    void testUpdateSubtask() {
        taskManager.addEpic(epic);
        subTask.setEpicId(epic.getId());
        taskManager.addSubtask(subTask);
        SubTask updatedSubTask = new SubTask("Updated SubTask", "Updated Description", TaskStatus.DONE, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));
        updatedSubTask.setId(subTask.getId());
        taskManager.updateSubTask(subTask, updatedSubTask);
        Optional<SubTask> retrievedSubTask = taskManager.getSubTaskById(subTask.getId());
        assertTrue(retrievedSubTask.isPresent(), "SubTask should be updated");
        assertEquals(updatedSubTask, retrievedSubTask.get(), "SubTask should be updated with new values");
    }

    @Test
    void testClearTask() {
        taskManager.addTask(task);
        taskManager.clearTask();
        assertTrue(taskManager.getTasks().isEmpty(), "Tasks should be cleared");
    }

    @Test
    void testClearSubtask() {
        taskManager.addEpic(epic);
        subTask.setEpicId(epic.getId());
        taskManager.addSubtask(subTask);
        taskManager.clearSubtask();
        assertTrue(taskManager.getSubtasks().isEmpty(), "Subtasks should be cleared");
        Optional<Epic> retrievedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(retrievedEpic.isPresent(), "Epic should still exist");
        assertTrue(retrievedEpic.get().getSubTaskIds().isEmpty(), "Epic's subtask list should be cleared");
    }

    @Test
    void testDeleteEpic() {
        taskManager.addEpic(epic);
        subTask.setEpicId(epic.getId());
        taskManager.addSubtask(subTask);
        taskManager.deleteEpic();
        assertTrue(taskManager.getEpics().isEmpty(), "Epics should be deleted");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Subtasks should be deleted with epics");
    }

    @Test
    void testGetTasks() {
        taskManager.addTask(task);
        Map<Integer, Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size(), "Should return one task");
        assertEquals(task, tasks.get(task.getId()), "Returned task should match the added task");

        // Граничный случай: пустой Map
        taskManager.clearTask();
        assertTrue(taskManager.getTasks().isEmpty(), "Should return empty map after clearing tasks");
    }

    @Test
    void testGetEpics() {
        taskManager.addEpic(epic);
        Map<Integer, Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size(), "Should return one epic");
        assertEquals(epic, epics.get(epic.getId()), "Returned epic should match the added epic");

        // Граничный случай: пустой Map
        taskManager.deleteEpic();
        assertTrue(taskManager.getEpics().isEmpty(), "Should return empty map after deleting epics");
    }

    @Test
    void testGetSubtasks() {
        taskManager.addEpic(epic);
        subTask.setEpicId(epic.getId());
        taskManager.addSubtask(subTask);
        Map<Integer, SubTask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size(), "Should return one subtask");
        assertEquals(subTask, subtasks.get(subTask.getId()), "Returned subtask should match the added subtask");

        // Граничный случай: пустой Map
        taskManager.clearSubtask();
        assertTrue(taskManager.getSubtasks().isEmpty(), "Should return empty map after clearing subtasks");
    }

    @Test
    void testGetPrioritizedTasks() {
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 8, 0));
        taskManager.addTask(task);
        taskManager.addTask(task2);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size(), "Should return two tasks");
        assertEquals(task2, prioritizedTasks.get(0), "Task with earlier start time should be first");
        assertEquals(task, prioritizedTasks.get(1), "Task with later start time should be second");
    }

    @Test
    void testGetSubtaskByEpic() {
        taskManager.addEpic(epic);
        subTask.setEpicId(epic.getId());
        taskManager.addSubtask(subTask);
        List<SubTask> subtasks = taskManager.getSubtaskByEpic(epic);
        assertEquals(1, subtasks.size(), "Should return one subtask for the epic");
        assertEquals(subTask, subtasks.get(0), "Returned subtask should match the added subtask");
    }

    @Test
    void testGetHistory() {
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "History should contain one task");
        assertEquals(task, history.get(0), "History should contain the viewed task");
    }

    // Тест на пересечение интервалов
    @Test
    void testTimeIntersection() {
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 9, 0));
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 9, 30));
        taskManager.addTask(task1);

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2),
                "Should throw exception due to time intersection");
    }

    @Test
    void testNoTimeIntersection() {
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 9, 0));
        Task task2 = new Task("Task 2", "Desc 2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));
        assertDoesNotThrow(() -> {
            taskManager.addTask(task1);
            taskManager.addTask(task2);
        }, "Tasks with non-overlapping times should be added without exception");
    }
}
