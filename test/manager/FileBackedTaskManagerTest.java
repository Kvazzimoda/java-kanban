package manager;

import data.*;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class FileBackedTaskManagerTest {

    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("tasksTest1", ".csv");
        FileBackedTaskManager manager1 = new FileBackedTaskManager(tempFile);
        // Тест 1: Сохранение и загрузка пустого файла

        manager1.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void testLoadMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("tasksTest3", ".csv");
        FileBackedTaskManager manager1 = getFileBackedTaskManager(tempFile);
        manager1.save(); // Сохраняем в файл

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем задачи
        assertEquals(1, loadedManager.getTasks().size(), "Должна быть 1 задача");
        Task loadedTask = loadedManager.getTasks().get(1);
        assertEquals("Task1", loadedTask.getTitle(), "Название задачи должно совпадать");
        assertEquals(TaskStatus.NEW, loadedTask.getStatus(), "Статус задачи должен совпадать");

        // Проверяем эпики
        assertEquals(1, loadedManager.getEpics().size(), "Должен быть 1 эпик");
        Epic loadedEpic = loadedManager.getEpics().get(2);
        assertEquals("Epic1", loadedEpic.getTitle(), "Название эпика должно совпадать");
        assertEquals(1, loadedEpic.getSubTaskIds().size(), "Должен быть 1 ID подзадачи");
        assertTrue(loadedEpic.getSubTaskIds().contains(3), "ID подзадачи должен быть 3");

        // Проверяем подзадачи
        assertEquals(1, loadedManager.getSubtasks().size(), "Должна быть 1 подзадача");
        SubTask loadedSubtask = loadedManager.getSubtasks().get(3);
        assertEquals("SubTask1", loadedSubtask.getTitle(), "Название подзадачи должно совпадать");
        assertEquals(TaskStatus.DONE, loadedSubtask.getStatus(), "Статус подзадачи должен совпадать");
        assertEquals(2, loadedSubtask.getEpicId(), "ID эпика подзадачи должен совпадать");
    }

    private static FileBackedTaskManager getFileBackedTaskManager(File tempFile) {
        FileBackedTaskManager manager1 = new FileBackedTaskManager(tempFile);
        // Тест 3: Загрузка нескольких задач
        Task task1 = new Task("Task1", "Description1", TaskStatus.NEW);
        task1.setId(1);
        Epic epic1 = new Epic("Epic1", "EpicDesc1");
        epic1.setId(2);
        SubTask subtask1 = new SubTask("SubTask1", "SubDesc1", TaskStatus.DONE, 2);
        subtask1.setId(3);

        manager1.addTask(task1);
        manager1.addEpic(epic1);
        manager1.addSubtask(subtask1);
        return manager1;
    }
}