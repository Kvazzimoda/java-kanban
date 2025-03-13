package manager;

import data.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    // Хранение задач различных типов
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected static int counterId = 1;

    private final HistoryManager historyManager;

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

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
        if (task.getStartTime() != null && tasks.values().stream().anyMatch(task::intersects)) {
            throw new IllegalArgumentException("Задача пересекается с другой по времени выполнения");
        }
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
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
        if (subtask.getStartTime() != null) {
            // Используем prioritizedTasks для проверки пересечений
            boolean hasIntersection = prioritizedTasks.stream()
                    .filter(t -> t.getId() != subtask.getId()) // Исключаем саму подзадачу
                    .anyMatch(t -> t.getStartTime() != null && subtask.intersects(t));
            if (hasIntersection) {
                System.out.println("Конфликт с задачей: " + prioritizedTasks.stream()
                        .filter(t -> t.getStartTime() != null && subtask.intersects(t))
                        .findFirst().orElse(null));
                throw new IllegalArgumentException("Подзадача пересекается с другой задачей по времени выполнения");
            }
            prioritizedTasks.add(subtask); // Добавляем подзадачу в приоритетный список
        }
        subtasks.put(id, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(id);
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
            if (epic.getStartTime() != null && !prioritizedTasks.contains(epic)) {
                prioritizedTasks.remove(epic); // Удаляем старый эпик
                updateEpicTimeFields(epic);    // Обновляем время эпика
                prioritizedTasks.add(epic);    // Добавляем обновленный эпик
            }
        }
    }

    protected void updateEpicTimeFields(Epic epic) {
        List<SubTask> subTasks = epic.getSubTaskIds().stream() // Получаем список ID подзадач
                .map(subtasks::get)  // Достаём сами подзадачи из Map
                .filter(Objects::nonNull)
                .filter(t -> t.getStartTime() != null) // Убираем подзадачи без времени
                .sorted(Comparator.comparing(Task::getStartTime)) // Сортируем по startTime
                .toList();

        if (subTasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            return;
        }

        LocalDateTime start = subTasks.get(0).getStartTime();
        LocalDateTime end = subTasks.get(subTasks.size() - 1).getEndTime();
        Duration duration = Duration.between(start, end);

        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(duration);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        List<Integer> subTaskIds = epic.getSubTaskIds();

        if (subTaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subTaskIds.stream()
                .map(subtasks::get)
                .allMatch(s -> s != null && s.getStatus() == TaskStatus.NEW);
        boolean allDone = subTaskIds.stream()
                .map(subtasks::get)
                .allMatch(s -> s != null && s.getStatus() == TaskStatus.DONE);

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        prioritizedTasks.removeIf(t -> t.getType() == TypeTask.TASK);
    }

    @Override
    public void clearSubtask() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
        }
        subtasks.clear();
        prioritizedTasks.removeIf(t -> t.getType() == TypeTask.SUBTASK);
    }

    @Override
    public void deleteEpic() { //удаляем все эпики и их подзадачи
        for (Epic epic : epics.values()) {
            for (int subTaskId : epic.getSubTaskIds()) {
                subtasks.remove(subTaskId);
                prioritizedTasks.removeIf(t -> t.getId() == subTaskId);
                historyManager.remove(subTaskId);
            }
        }
        epics.clear();
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return Optional.ofNullable(task);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return Optional.ofNullable(epic);
    }

    @Override
    public Optional<SubTask> getSubTaskById(int id) {
        SubTask subTask = subtasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return Optional.ofNullable(subTask);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subTaskId : epic.getSubTaskIds()) {
                subtasks.remove(subTaskId);
                prioritizedTasks.removeIf(t -> t.getId() == subTaskId);
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
                updateEpicTimeFields(epic);
            }
            subtasks.remove(id);
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
        }
    }

    @Override
    public List<SubTask> getSubtaskByEpic(Epic epic) {
        List<SubTask> epicSubtasks = new ArrayList<>();
        if (epic != null) {
            for (Integer subTaskId : epic.getSubTaskIds()) {
                SubTask subTask = subtasks.get(subTaskId);
                if (subTask != null) {
                    epicSubtasks.add(subTask);
                }
            }
        }
        return epicSubtasks;
    }

    @Override
    public void updateTask(Task task, Task newTask) { // обновление задачи
        newTask.setId(task.getId());
        if (newTask.getStartTime() != null && tasks.values().stream()
                .filter(t -> t.getId() != task.getId())
                .anyMatch(task::intersects)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается с другой по времени выполнения");
        }
        prioritizedTasks.remove(task);
        tasks.put(task.getId(), newTask);
        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }
    }

    @Override
    public void updateEpic(Epic epic, String newTitle, String newDescription) {
        epic.setTitle(newTitle);
        epic.setDescription(newDescription);
        updateEpicStatus(epic);
        updateEpicTimeFields(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask, SubTask newSubTask) {
        if (subTask == null || newSubTask == null) {
            throw new IllegalArgumentException("SubTask или newSubTask не может быть null");
        }
        newSubTask.setId(subTask.getId());
        if (newSubTask.getStartTime() != null) {
            boolean hasIntersection = tasks.values().stream()
                    .anyMatch(newSubTask::intersects) ||
                    subtasks.values().stream()
                            .filter(s -> s.getId() != newSubTask.getId())
                            .anyMatch(newSubTask::intersects);
            if (hasIntersection) {
                throw new IllegalArgumentException("Обновлённая подзадача пересекается с другой по времени выполнения");
            }
        }
        if (subTask.getStartTime() != null) {
            prioritizedTasks.remove(subTask);
        }
        subtasks.put(subTask.getId(), newSubTask);
        Epic epic = epics.get(newSubTask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
        }
        if (newSubTask.getStartTime() != null) {
            prioritizedTasks.add(newSubTask);
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


