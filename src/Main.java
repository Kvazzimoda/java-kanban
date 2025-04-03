
import data.*;
import manager.Managers;
import manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // 1. Создание двух задач
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 14, 0));
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 2, 16, 30));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // 1. Создание эпиков
        Epic epicWithSubtasks = new Epic("Эпик 1", "Эпик с подзадачами");
        taskManager.addEpic(epicWithSubtasks);
        Epic epicWithoutSubtasks = new Epic("Эпик 2", "Эпик с подзадачей для теста");
        taskManager.addEpic(epicWithoutSubtasks);


        // 1. Создание подзадач для первого эпика
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epicWithSubtasks.getId(),
                Duration.ofMinutes(90), LocalDateTime.of(2025, 3, 12, 10, 0));
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание 2", TaskStatus.NEW, epicWithSubtasks.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 11, 0));
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание 3", TaskStatus.NEW, epicWithoutSubtasks.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 3, 10, 0));
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3);

        // 2. Запрос созданных задач в разном порядке


        System.out.println("Вызов задач по приоритету");
        System.out.println(taskManager.getPrioritizedTasks());

        /* System.out.println("Удаляем subtaskи ");
        taskManager.deleteSubTaskById(subTask1.getId());

*/
        System.out.println("Удаляем epic");
        taskManager.deleteEpicById(epicWithSubtasks.getId());
        System.out.println(taskManager.getPrioritizedTasks());
        // 4. Вывод истории просмотров
        printHistory(taskManager);

        // 5. Удаление задачи, проверка истории
        taskManager.deleteTaskById(task1.getId());
        System.out.println("\nПосле удаления задачи 1:");
        System.out.println("Tasks: " + taskManager.getTasks());
        System.out.println("Prioritized Tasks: " + taskManager.getPrioritizedTasks());
        printHistory(taskManager);

        // 6. Удаление эпика с подзадачами, проверка истории
        taskManager.deleteEpicById(epicWithSubtasks.getId());
        System.out.println("\nПосле удаления эпика с подзадачами:");
        System.out.println("Tasks: " + taskManager.getTasks());
        System.out.println("Epics: " + taskManager.getEpics());
        System.out.println("Subtasks: " + taskManager.getSubtasks());
        System.out.println("Prioritized Tasks: " + taskManager.getPrioritizedTasks());
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager taskManager) {
        List<Task> history = taskManager.getHistory();
        System.out.println("История просмотров:");
        for (Task task : history) {
            System.out.println(task);
        }
    }
}


