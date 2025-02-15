
import data.*;
import manager.Managers;
import manager.TaskManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // 1. Создание двух задач
        Task task1 = new Task("Задача 1", "Описание 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // 1. Создание эпиков
        Epic epicWithSubtasks = new Epic("Эпик 1", "Эпик с подзадачами");
        Epic epicWithoutSubtasks = new Epic("Эпик 2", "Эпик без подзадач");
        taskManager.addEpic(epicWithSubtasks);
        taskManager.addEpic(epicWithoutSubtasks);

        // 1. Создание подзадач для первого эпика
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание 1", TaskStatus.NEW, epicWithSubtasks.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание 2", TaskStatus.NEW, epicWithSubtasks.getId());
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание 3", TaskStatus.NEW, epicWithSubtasks.getId());
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3);

        // 2. Запрос созданных задач в разном порядке
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epicWithSubtasks.getId());
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getEpicById(epicWithoutSubtasks.getId());

        // 3. Вывод истории просмотров
        printHistory(taskManager);

        // 4. Удаление задачи, проверка истории
        taskManager.deleteTaskById(task1.getId());
        System.out.println("\nПосле удаления задачи 1:");
        printHistory(taskManager);

        // 5. Удаление эпика с подзадачами, проверка истории
        taskManager.deleteEpicById(epicWithSubtasks.getId());
        System.out.println("\nПосле удаления эпика с подзадачами:");
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


