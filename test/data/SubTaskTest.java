package data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubSubTaskTest {

    @Test
    public void testSubTaskEqualityBasedOnId() {
        Epic epic = new Epic("Parent Epic", "Parent Epic Description");
        epic.setId(1); // Устанавливаем ID для эпика

        SubTask subTask1 = new SubTask("SubTask 1", "Description of SubTask 1", epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "Description of SubTask 2", epic.getId());

        // Устанавливаем одинаковый id
        subTask1.setId(1);
        subTask2.setId(1);

        // Проверяем, что подзадачи  равны
        assertEquals(subTask1, subTask2, "Экземпляры подзадачи с одинаковым идентификатором должны быть равны");

    }

    @Test
    void newSubTask() {
        SubTask subtask = new SubTask("subtask 1", "Description", TaskStatus.NEW, 1);
        assertEquals("subtask 1", subtask.getTitle(), "Ошибка установки наименования subtask");
        assertEquals("Description", subtask.getDescription(), "Ошибка установки описания subtask");
        assertEquals(TaskStatus.NEW, subtask.getStatus(), "Ошибка установки описания subtask");
    }
}