
import data.*;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Заголовок задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Заголовок задачи 2", TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        System.out.println(taskManager.getTasks());

        Epic epic1 = new Epic("Эпик 1", "Заголовок эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Заголовок эпика 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        System.out.println(taskManager.getEpics());

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                TaskStatus.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2",
                TaskStatus.NEW, epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3",
                TaskStatus.NEW, epic2.getId());
        SubTask subTask4 = new SubTask("Подзадача 4", "Описание подзадачи 4",
                TaskStatus.NEW, epic2.getId());
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3);
        taskManager.addSubtask(subTask4);

        System.out.println(taskManager.getSubtasks());
        System.out.println("Epic после добавления SubTask: " + taskManager.getEpics());

        Task task1Update = new Task("Обновление задачи 1", "Добавили новую инфу в задачу",
                TaskStatus.DONE);
        SubTask subTask1Update = new SubTask("Обновление подзадачи 1", "Внесли изменения в подзадачу 1",
                TaskStatus.DONE, epic1.getId());
        SubTask subTask3Update = new SubTask("Обновление подзадачи 3", "Внесли изменения в подзадачу 3",
                TaskStatus.DONE, epic2.getId());

        System.out.println("--------------+++++++++++++++-----------------");
        taskManager.updateTask(task1, task1Update); // обновление задачи

        taskManager.updateSubTask(subTask1, subTask1Update); // Обновление подзадачи
        taskManager.updateSubTask(subTask3, subTask3Update);

        System.out.println(taskManager.getEpics());

        taskManager.updateEpic(epic1, "Обнова 1 Эпика", "Добавили новые плюшки, обновили подзадачу"
        );
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println("--------------------------------------------");

        System.out.println("------------------------------------------++");
        SubTask subTask2Update = new SubTask("Обновили подзадачу 2", "Внесли правки, поправили кое-что",
                TaskStatus.DONE, epic1.getId());
        taskManager.updateSubTask(subTask2, subTask2Update);
        System.out.println(taskManager.getSubtaskByEpic(epic1));
        System.out.println(taskManager.getEpics());
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getEpicById(4));
        System.out.println(taskManager.getSubTaskById(7));
        taskManager.deleteEpicById(3);
        taskManager.deleteSubTaskById(4);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);

        taskManager.printAllTasks(taskManager);

        System.out.println("============================================");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println("============================================");

        taskManager.printAllTasks(taskManager);

    }
}
