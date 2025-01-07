import managementTool.TaskManager;
import typesOfTasks.Task;
import typesOfTasks.Epic;
import typesOfTasks.SubTask;
import typesOfTasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

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
                TaskStatus.NEW, epic1.getId());
        SubTask subTask4 = new SubTask("Подзадача 4", "Описание подзадачи 4",
                TaskStatus.NEW, epic1.getId());
        taskManager.addSubtask(subTask1);
        taskManager.addSubtask(subTask2);
        taskManager.addSubtask(subTask3);
        taskManager.addSubtask(subTask4);

        epic1.addSubtask(subTask1);
        epic1.addSubtask(subTask2);
        epic2.addSubtask(subTask3);
        epic2.addSubtask(subTask4);

        System.out.println(taskManager.getSubtasks());

        Task task1Update = new Task("Обновление задачи 1", "Добавление новой инфы в задачу",
                TaskStatus.DONE);
        SubTask subTask1Update = new SubTask("Обновление подзадачи 1", "Внесли изменения в подзадачу 1",
                TaskStatus.DONE, epic1.getId());
        SubTask subTask2Update = new SubTask("Обновление подзадачи 2", "Внесли изменения в подзадачу 2",
                TaskStatus.DONE, epic2.getId());


         // удаление задачи по ID

        System.out.println("-------------------------------------------");
        taskManager.updateTask(task1,task1Update); // обновление задачи

        taskManager.updateSubTask(subTask1, subTask1Update); // Обновление подзадачи
        taskManager.updateSubTask(subTask2, subTask2Update);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println("--------------------------------------------");
        System.out.println(taskManager.getSubtaskByEpic(epic1));

        System.out.println(taskManager.getTaskById(2));
        taskManager.deleteTaskById(2);
        System.out.println(taskManager.getTasks());
    }
}
