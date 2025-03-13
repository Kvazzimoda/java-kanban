
import data.*;
import manager.Managers;
import manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // 1. Создание двух задач
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 14, 0));
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 11, 30));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // 1. Создание эпиков
        Epic epicWithSubtasks = new Epic("Эпик 1", "Эпик с подзадачами");
        taskManager.addEpic(epicWithSubtasks);
        Epic epicWithoutSubtasks = new Epic("Эпик 2", "Эпик с подзадачей для теста");
        taskManager.addEpic(epicWithoutSubtasks);


        // 1. Создание подзадач для первого эпика
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epicWithSubtasks.getId(),
                Duration.ofMinutes(45), LocalDateTime.of(2025, 3, 12, 9, 15));
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание 2", TaskStatus.NEW, epicWithSubtasks.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 10, 0));
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание 3", TaskStatus.NEW, epicWithoutSubtasks.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 13, 10, 0));
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3);

        // 2. Запрос созданных задач в разном порядке
        Optional<Task> optTask1 = taskManager.getTaskById(task1.getId());
        Optional<Epic> optEpicWithSubtasks = taskManager.getEpicById(epicWithSubtasks.getId());
        Optional<SubTask> optSubTask3 = taskManager.getSubTaskById(subTask3.getId());
        Optional<Task> optTask2 = taskManager.getTaskById(task2.getId());
        Optional<SubTask> optSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        Optional<SubTask> optSubTask1 = taskManager.getSubTaskById(subTask1.getId());
        Optional<Epic> optEpicWithoutSubtasks = taskManager.getEpicById(epicWithoutSubtasks.getId());

        // 3. Вызов задач по приоритету
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


