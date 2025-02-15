package manager;

import data.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(SubTask subtask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubtasks();

    void clearTask();

    void clearSubtask();

    void deleteEpic();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubTaskById(int id);

    List<SubTask> getSubtaskByEpic(Epic epic);

    void updateTask(Task task, Task newTask);

    void updateEpic(Epic epic, String newTitle, String newDescription);

    void updateSubTask(SubTask subTask, SubTask newSubTask);

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();

}
