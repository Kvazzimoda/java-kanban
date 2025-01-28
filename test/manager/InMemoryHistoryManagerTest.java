package manager;

import data.Task;
import data.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {


    //Тестируем, что задачи попадают в историю и при превышении лимита первая удаляется
    @Test
    void getHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task("TestingHistory_1", "TestingHistoryDescription_1", TaskStatus.NEW);
        Task task2 = new Task("TestingHistory_2", "TestingHistoryDescription_2", TaskStatus.NEW);
        Task task3 = new Task("TestingHistory_3", "TestingHistoryDescription_3", TaskStatus.NEW);
        Task task4 = new Task("TestingHistory_4", "TestingHistoryDescription_4", TaskStatus.NEW);
        Task task5 = new Task("TestingHistory_5", "TestingHistoryDescription_4", TaskStatus.NEW);
        Task task6 = new Task("TestingHistory_6", "TestingHistoryDescription_4", TaskStatus.NEW);
        Task task7 = new Task("TestingHistory_7", "TestingHistoryDescription_4", TaskStatus.NEW);
        Task task8 = new Task("TestingHistory_8", "TestingHistoryDescription_4", TaskStatus.NEW);
        Task task9 = new Task("TestingHistory_9", "TestingHistoryDescription_4", TaskStatus.NEW);
        Task task10 = new Task("TestingHistory_10", "TestingHistoryDescription_4", TaskStatus.NEW);
        Task task11 = new Task("TestingHistory_11", "TestingHistoryDescription_4", TaskStatus.NEW);

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);
        historyManager.add(task8);
        historyManager.add(task9);
        historyManager.add(task10);
        historyManager.add(task11);

        final List<Task> history = historyManager.getHistory();
        final List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(task2);
        expectedHistory.add(task3);
        expectedHistory.add(task4);
        expectedHistory.add(task5);
        expectedHistory.add(task6);
        expectedHistory.add(task7);
        expectedHistory.add(task8);
        expectedHistory.add(task9);
        expectedHistory.add(task10);
        expectedHistory.add(task11);
        assertNotNull(history, "История не пустая.");
        assertArrayEquals(expectedHistory.toArray(), history.toArray());
    }


}