package manager;

import data.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void shouldReturnNonNullTaskManager() {

        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Экземпляр TaskManager не должен возвращать null");
    }

    @Test
    public void shouldReturnNonNulltaskManager() {

        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Экземпляр historyManager не должен возвращать null");
    }

    @Test
    public void shouldReturnNewTaskManagerInstanceEachTime() {

        TaskManager firstInstance = Managers.getDefault();
        TaskManager secondInstance = Managers.getDefault();

        assertNotSame(firstInstance, secondInstance, "Каждый вызов getDefault должен возвращать " +
                "новый экземпляр TaskManager");
    }

    @Test
    public void shouldReturnNewtaskManagerInstanceEachTime() {

        HistoryManager firstInstance = Managers.getDefaultHistory();
        HistoryManager secondInstance = Managers.getDefaultHistory();

        assertNotSame(firstInstance, secondInstance, "Каждый вызов функции getDefaultHistory должен " +
                "возвращать новый экземпляр HistoryManager");
    }

    @Test
    public void taskManagerInstanceShouldBeFunctional() {

        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Task 1", "Description 1");

        taskManager.addTask(task);

        assertEquals(1, taskManager.getTasks().size(), "Диспетчер задач должен корректно хранить задачи");
        assertEquals(task, taskManager.getTasks().getFirst(), "Диспетчер задач должен вернуть правильную задачу");
    }

    @Test
    public void historyManagerInstanceShouldBeFunctional() {

        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task 1", "Description 1");
        task.setId(1);

        historyManager.add(task);
        var history = historyManager.getHistory();

        assertNotNull(history, "history не должна возвращать null");
        assertEquals(1, history.size(), "HistoryManager должен корректно хранить задачи");
        assertEquals(task, history.getFirst(), "HistoryManager должен возвращать правильную задачу из истории");
    }
}