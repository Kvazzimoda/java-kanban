package manager;

import data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager(); // Создаём новый экземпляр для каждого теста
    }

    @Test
    public void shouldAddAndRetrieveTaskById() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertNotNull(retrievedTask, "Задача не должна быть нулевой");
        assertEquals(task, retrievedTask, "Извлеченная задача должна соответствовать добавленной задаче");
    }

    @Test
    public void shouldAddAndRetrieveEpicById() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(retrievedEpic, "Значение эпика не должно быть равным null");
        assertEquals(epic, retrievedEpic, "Извлеченный эпик должен соответствовать добавленному эпику");
    }

    @Test
    public void shouldAddAndRetrieveSubTaskById() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Subtask 1", "Subtask Description", epic.getId());
        taskManager.addSubtask(subTask);

        SubTask retrievedSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(retrievedSubTask, "SubTask не должен быть равен null");
        assertEquals(subTask, retrievedSubTask, "Полученный SubTask должен соответствовать добавленному SubTask");
    }

    @Test
    public void shouldAddSubTaskToEpicAndUpdateEpicStatus() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Subtask 1", "Subtask Description", epic.getId());
        taskManager.addSubtask(subTask);

        List<SubTask> subTasks = taskManager.getSubtaskByEpic(epic);

        assertNotNull(subTasks, "Список подзадач не должен быть пустым");
        assertEquals(1, subTasks.size(), "Epic должен содержать одну подзадачу");
        assertEquals(subTask, subTasks.get(0), "Подзадача должна соответствовать добавленной подзадаче");
    }

    @Test
    public void testEpicEqualityBasedOnId() {
        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description of Epic 2");

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Экземпляры Epic с одинаковым идентификатором должны быть одинаковыми");
    }

    @Test
    public void shouldNotConflictBetweenGivenIdAndGeneratedId() {
        Task taskWithGivenId = new Task("Task 1", "Description 2", TaskStatus.NEW, 3);
        taskManager.addTask(taskWithGivenId);

        Task generatedTask = new Task("Task 2", "Description 2", TaskStatus.NEW);
        taskManager.addTask(generatedTask);

        Task retrievedByGivenId = taskManager.getTaskById(taskWithGivenId.getId());
        Task retrievedByGeneratedId = taskManager.getTaskById(generatedTask.getId());

        assertNotNull(retrievedByGivenId, "Задача с заданным ID должна быть доступна");
        assertNotNull(retrievedByGeneratedId, "Задача со сгенерированным ID должна быть доступна");
        assertNotEquals(retrievedByGivenId, retrievedByGeneratedId, "Конфликт задач с заданным и сгенерированным ID");
    }

    @Test
    public void shouldNotChangeTaskDataWhenAddedToManager() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertEquals(task.getTitle(), retrievedTask.getTitle(), "Название не должно меняться");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описание не должно изменяться");
        assertEquals(task.getStatus(), retrievedTask.getStatus(), "Статус не должен меняться");
        assertEquals(task.getId(), retrievedTask.getId(), "ID не должен меняться");
    }

    @Test
    public void shouldPreserveTaskDataInHistoryManager() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW, 1);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История не должна быть пустой");
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.get(0), "Задача в истории должна соответствовать добавленной задаче");
    }

    @Test
    public void shouldRemoveSubtaskAndUpdateEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask 1", "Subtask Description", epic.getId());
        taskManager.addSubtask(subTask);

        taskManager.deleteSubTaskById(subTask.getId());

        List<SubTask> subTasks = taskManager.getSubtaskByEpic(epic);
        assertTrue(subTasks.isEmpty(), "После удаления подзадачи, список подзадач у эпика должен быть пустым");
    }

    @Test
    public void shouldUpdateTaskFieldsCorrectly() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.addTask(task);

        task.setTitle("Updated Task 1");
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.IN_PROGRESS);


        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task 1", updatedTask.getTitle(), "Название должно быть обновлено");
        assertEquals("Updated Description", updatedTask.getDescription(), "Описание должно быть обновлено");
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus(), "Статус должен быть обновлён");
    }
}
