package data;


import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubSubTaskTest {

    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(); // Инициализация TaskManager
        epic = new Epic("Parent Epic", "Parent Epic Description");
        epic.setId(1); // Устанавливаем ID для эпика
        taskManager.addEpic(epic); // Добавляем эпик в менеджер
    }

    @Test
    public void testSubTaskEqualityBasedOnId() {
        Epic epic = new Epic("Parent Epic", "Parent Epic Description");
        epic.setId(1); // Устанавливаем ID для эпика

        SubTask subTask1 = new SubTask("SubTask 1", "Description of SubTask 1", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));
        SubTask subTask2 = new SubTask("SubTask 2", "Description of SubTask 2", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));

        // Устанавливаем одинаковый id
        subTask1.setId(1);
        subTask2.setId(1);

        // Проверяем, что подзадачи  равны
        assertEquals(subTask1, subTask2, "Экземпляры подзадачи с одинаковым идентификатором должны быть равны");

    }

    @Test
    void newSubTask() {
        SubTask subtask = new SubTask("subtask 1", "Description", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 10, 0));
        taskManager.addSubtask(subtask);
        assertEquals("subtask 1", subtask.getTitle(), "Ошибка установки наименования subtask");
        assertEquals("Description", subtask.getDescription(), "Ошибка установки описания subtask");
        assertEquals(TaskStatus.NEW, subtask.getStatus(), "Ошибка установки описания subtask");
    }
}