package manager;

import data.SubTask;
import data.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    void setUp() {
        super.setUp();
         InMemoryTaskManager.setCounterId(0); // Сброс счётчика ID перед каждым тестом
    }

    // Тесты для расчёта статуса Epic
    @Test
    void testEpicStatusAllSubtasksNew() {
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "Desc 1", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 9, 0));
        SubTask subTask2 = new SubTask("SubTask 2", "Desc 2", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Epic status should be NEW when all subtasks are NEW");
    }

    @Test
    void testEpicStatusAllSubtasksDone() {
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "Desc 1", TaskStatus.DONE, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 9, 0));
        SubTask subTask2 = new SubTask("SubTask 2", "Desc 2", TaskStatus.DONE, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Epic status should be DONE when all subtasks are DONE");
    }

    @Test
    void testEpicStatusSubtasksNewAndDone() {
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "Desc 1", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 9, 0));
        SubTask subTask2 = new SubTask("SubTask 2", "Desc 2", TaskStatus.DONE, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS when subtasks are NEW and DONE");
    }

    @Test
    void testEpicStatusSubtasksInProgress() {
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "Desc 1", TaskStatus.IN_PROGRESS, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 9, 0));
        SubTask subTask2 = new SubTask("SubTask 2", "Desc 2", TaskStatus.IN_PROGRESS, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS when subtasks are IN_PROGRESS");
    }
}
