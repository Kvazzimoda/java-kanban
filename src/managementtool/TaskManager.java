package managementtool;

import typesoftasks.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class TaskManager {
    // Хранение задач различных типов
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private static int counterId = 1;

    private int generateId() {
        return counterId++;
    }


    public void addTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
    }

    public void addEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
    }

    public void addSubtask(SubTask subtask) {
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(id);
            updateEpicStatus(epic);
        }
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
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    public void deleteEpic() { //удаляем все эпики и их подзадачи
        for (Epic epic : epics.values()) {
            for (int subTaskId : epic.getSubTaskIds()) {
                subtasks.remove(subTaskId);
            }
        }
        epics.clear();
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

    public void deleteTaskById(int id) {
            tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subTaskId : epic.getSubTaskIds()) {
                subtasks.remove(subTaskId);
            }
            epics.remove(id);
        }
    }

    public void deleteSubTaskById(int id) {
        SubTask subtask = subtasks.get(id);
        if (subtask != null) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubTaskId(id);
            updateEpicStatus(epic);
        }
        subtasks.remove(id);
        }
    }

    public List<SubTask> getSubtaskByEpic(Epic epic) {
        List<SubTask> subTaskList = new ArrayList<>();
        for (int subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subtasks.get(subTaskId);
            if (subTask != null) {
                subTaskList.add(subTask);
            }
        }
        return subTaskList;
    }

    public void updateTask(Task task, Task newTask) { // обновление задачи
        newTask.setId(task.getId());
        tasks.put(task.getId(), newTask);
    }

    public void updateEpic(Epic epic, String newTitle, String newDescription) { // добавил метод обновления эпика
        // Обновляем только заголовок и описание
        epic.setTitle(newTitle);
        epic.setDescription(newDescription);

        // Так как подзадачи не изменяются, статус остается актуальным
        updateEpicStatus(epic);
    }

    public void updateSubTask(SubTask subTask, SubTask newSubTask) { //обновление подзадачи
        newSubTask.setId(subTask.getId());
        subtasks.put(subTask.getId(), newSubTask);
        Epic epic = epics.get(newSubTask.getEpicId());
        if (epic != null) {
            epic.removeSubTaskId(subTask.getId());
                        epic.addSubtaskId(newSubTask.getId());
                      epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    //


        private void updateEpicStatus(Epic epic) {
            List<Integer> subTaskIds = epic.getSubTaskIds();

            if (subTaskIds.isEmpty()) {
                epic.setStatus(TaskStatus.NEW);
                return;
            }

            boolean allNew = true;
            boolean allDone = true;

            for (int subTaskId : subTaskIds) {
                SubTask subTask = subtasks.get(subTaskId);
                if (subTask != null) {
                    if (subTask.getStatus() != TaskStatus.NEW) {
                        allNew = false;
                    }
                    if (subTask.getStatus() != TaskStatus.DONE) {
                        allDone = false;
                    }
                }
            }

            if (allNew) {
                epic.setStatus(TaskStatus.NEW);
            } else if (allDone) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
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


