package manager;

import data.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    // Хранение задач различных типов
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected static int counterId = 1;

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private int generateId() {
        return counterId++;
    }

    public static void setCounterId(int maxId) {
        if (maxId >= counterId) {
            counterId = maxId + 1;
        }
    }

    // Проверяем, что task является экземпляром класса Task
    @Override
    public void addTask(Task task) {
        if (task.getType() != TypeTask.TASK) {
            return;
        }
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getType() != TypeTask.EPIC) {
            return;
        }
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void addSubtask(SubTask subtask) {
        if (subtask.getType() != TypeTask.SUBTASK) {
            return;
        }
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(id);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateEpicStatus(Epic epic) {
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
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, SubTask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void clearTask() {
        tasks.clear();
    }

    @Override
    public void clearSubtask() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void deleteEpic() { //удаляем все эпики и их подзадачи
        for (Epic epic : epics.values()) {
            for (int subTaskId : epic.getSubTaskIds()) {
                subtasks.remove(subTaskId);
            }
        }
        epics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subtasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subTaskId : epic.getSubTaskIds()) {
                subtasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubTaskId(id);
                updateEpicStatus(epic);
            }
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
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

    @Override
    public void updateTask(Task task, Task newTask) { // обновление задачи
        newTask.setId(task.getId());
        tasks.put(task.getId(), newTask);
    }

    @Override
    public void updateEpic(Epic epic, String newTitle, String newDescription) { // добавил метод обновления эпика
        // Обновляем только заголовок и описание
        epic.setTitle(newTitle);
        epic.setDescription(newDescription);


        updateEpicStatus(epic);
    }

    @Override
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

    @Override
    public String toString() {
        return "manager.TaskManager{" + '\'' +
                "Tasks=" + tasks + '\'' +
                "Epics=" + epics + '\'' +
                "SubTask=" + subtasks +
                '}' + "\n";
    }
}


