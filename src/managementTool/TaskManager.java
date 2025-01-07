package managementTool;

import typesOfTasks.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class TaskManager {
    // Хранение задач различных типов
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private static int counterId = 1;

    static int generateId() {
        return counterId++;
    }

    public void addTask(Task task) {
        task.setId(counterId);
        tasks.put(generateId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(counterId);
        epics.put(generateId(), epic);
    }

    public void addSubtask(SubTask subtask) {
        subtask.setId(counterId);
        subtasks.put(generateId(), subtask);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearTask() {
        tasks.clear();
    }

    public void clearSubtask() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            epic.updateStatus();
        }
        subtasks.clear();
    }

    public void deleteEpic() { //удаляем эпик и его подзадачу
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public SubTask getSubTaskById(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    public void deleteTaskById(int id) { // Удаление task/epic/subtask по ID
        if (tasks.containsKey(id)){
            tasks.remove(id);
        } else
        if (subtasks.containsKey(id)) {
            SubTask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getId());
            epic.removeSubTask(subtask);
            subtasks.remove(id);
            epic.updateStatus();
        } else if (epics.containsKey(id) ) {
            Epic epic = epics.get(id);
            epic.clearSubTasks();
            epics.remove(id);
        }
    }

    public List<SubTask> getSubtaskByEpic(Epic epic) {
       return epic.getSubtasks();
    }

    public void updateTask(Task task, Task newTask) { // обновление задачи
        newTask.setId(task.getId());
        tasks.put(task.getId(), newTask);
    }

    public void updateSubTask(SubTask subTask, SubTask newSubTask) { //обновление подзадачи
        newSubTask.setId(subTask.getId());
        subtasks.put(subTask.getId(), newSubTask);
        Epic epic = epics.get(newSubTask.getEpicId());
        epic.removeSubTask(subTask);
        epic.addSubtask(newSubTask);
        epics.put(epic.getId(), epic);
    }



    @Override
    public String toString() {
        return "managementTool.TaskManager{" + '\'' +
                "Tasks=" + tasks + '\'' +
                "Epics=" + epics + '\'' +
                "SubTask=" + subtasks +
                '}' + "\n";
    }
}

