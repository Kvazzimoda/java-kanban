package manager;

import data.Task;
import data.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            file = Files.createTempFile("test", ".csv").toFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create temp file", e);
        }
        return new FileBackedTaskManager(file);
    }

    @BeforeEach
    void setUp() {
        super.setUp();
        InMemoryTaskManager.setCounterId(0); // Сброс счётчика ID
    }

    @Test
    void testFileNotFoundException() {
        File invalidFile = new File("/invalid/path/tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile); // Создаём объект
        assertThrows(ManagerSaveException.class, () -> manager.addTask(new Task("Test Task", "Description", TaskStatus.NEW)),
                "Should throw ManagerSaveException for invalid file path");
    }

    @Test
    void testFilePermissionException() throws IOException {
        File readOnlyFile = Files.createTempFile("readonly", ".csv").toFile();
        readOnlyFile.setReadOnly();
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager manager = new FileBackedTaskManager(readOnlyFile);
            manager.addTask(task); // Попытка записи
        }, "Should throw ManagerSaveException when file is read-only");
    }

    @Test
    void testSuccessfulFileWrite() {
        assertDoesNotThrow(() -> {
            taskManager.addTask(task);
            taskManager.addEpic(epic);
        }, "Should successfully write to file");
        assertTrue(file.exists(), "File should exist after writing");
        assertTrue(file.length() > 0, "File should contain data");
    }
}