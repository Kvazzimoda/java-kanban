package manager;

import data.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,title,status,description,duration,startTime,epic\\n");

            for (Task task : getTasks().values()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getEpics().values()) {
                writer.write(toString(epic) + "\n");
            }

            for (SubTask subTask : getSubtasks().values()) {
                writer.write(toString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении задач в файл: " + file.getPath(), e);
        }
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        sb.append(task.getDuration() != null ? task.getDuration().toMinutes() : "").append(",");
        sb.append(task.getStartTime() != null ? task.getStartTime().format(FORMATTER) : "");
        if (task.getType() == TypeTask.SUBTASK) {
            sb.append(",").append(((SubTask) task).getEpicId());
        }
        return sb.toString();
    }

    private Task fromString(String value) {
        String[] parts = value.split(",", 8);
        if (parts.length < 5) {
            throw new IllegalArgumentException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        TypeTask type = TypeTask.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        Duration duration = parts[5].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(parts[5]));
        LocalDateTime startTime = parts[6].isEmpty() ? null : LocalDateTime.parse(parts[6], FORMATTER);
        String epicValue = parts.length > 7 ? parts[7].trim() : null;

        return switch (type) {
            case TASK -> new Task(title, description, status, id, duration, startTime);
            case EPIC -> new Epic(id, title, description, status);
            case SUBTASK -> {
                if (epicValue == null || epicValue.isEmpty()) {
                    throw new IllegalArgumentException("Для подзадачи не указан ID эпика: " + value);
                }
                int epicId = Integer.parseInt(epicValue);
                yield new SubTask(id, title, description, status, epicId, duration, startTime);
            }
        };
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                return manager;
            }

            int maxId = 0;
            // Первый проход: определяем максимальный ID
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0]);
                    if (id > maxId) {
                        maxId = id;
                    }
                }
            }

            // Устанавливаем counterId на основе максимального ID
            setCounterId(maxId);
            System.out.println("Установлен counterId: " + counterId);

            // Второй проход: добавляем задачи напрямую в Map с сохранёнными ID
            Map<Integer, Epic> epics = new HashMap<>();
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    Task task = manager.fromString(line);
                    System.out.println("Загружаем задачу: " + task);
                    TypeTask type = task.getType(); // Получаем тип задачи через getType()

                    switch (type) {
                        case TASK:
                            manager.getTasks().put(task.getId(), task);
                            if (task.getStartTime() != null) {
                                manager.prioritizedTasks.add(task);
                            }
                            break;
                        case EPIC:
                            Epic epic = (Epic) task; // Приводим task к Epic
                            epics.put(epic.getId(), epic); // Добавляем Epic в epics
                            manager.getEpics().put(epic.getId(), epic); // Добавляем Epic в менеджер
                            break;
                        case SUBTASK:
                            SubTask subTask = (SubTask) task;
                            manager.getSubtasks().put(subTask.getId(), subTask);
                            // Обновляем список подзадач в эпике
                            Epic epicForSubtask = epics.get(subTask.getEpicId());
                            if (epicForSubtask != null) {
                                epicForSubtask.addSubtaskId(subTask.getId());
                                manager.updateEpicStatus(epicForSubtask);
                                manager.updateEpicTimeFields(epicForSubtask);
                            }
                            if (subTask.getStartTime() != null) {
                                manager.prioritizedTasks.add(subTask);
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке задач из файла: " + file.getPath(), e);
        }
        return manager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(SubTask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public void clearSubtask() {
        super.clearSubtask();
        save();
    }

    @Override
    public void deleteEpic() {
        super.deleteEpic();
        save();
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public Optional<SubTask> getSubTaskById(int id) {
        return super.getSubTaskById(id);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public List<SubTask> getSubtaskByEpic(Epic epic) {
        return super.getSubtaskByEpic(epic);
    }

    @Override
    public void updateTask(Task task, Task newTask) {
        super.updateTask(task, newTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic, String newTitle, String newDescription) {
        super.updateEpic(epic, newTitle, newDescription);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask, SubTask newSubTask) {
        super.updateSubTask(subTask, newSubTask);
        save();
    }

    public static void main(String[] args) {
        try {
            File tempFile = File.createTempFile("tasks", ".csv");
            FileBackedTaskManager manager1 = new FileBackedTaskManager(tempFile);

            // Создаём задачи с duration и startTime
            Task task1 = new Task("Task1", "Description1", TaskStatus.NEW,
                    Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 11, 8, 0));
            Task task2 = new Task("Task2", "Description2", TaskStatus.IN_PROGRESS,
                    Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 12, 9, 30));
            Epic epic1 = new Epic("Epic1", "Epic Description1");
            manager1.addTask(task1);
            manager1.addTask(task2);
            manager1.addEpic(epic1);
            int epicId = epic1.getId(); // Получаем сгенерированный ID после добавления

            SubTask subtask1 = new SubTask("SubTask1", "SubTask Description1", TaskStatus.DONE, epicId,
                    Duration.ofMinutes(45), LocalDateTime.of(2025, 3, 13, 5, 0));
            SubTask subtask2 = new SubTask("SubTask2", "SubTask Description2", TaskStatus.NEW, epicId,
                    Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 14, 10, 0));

            // Добавляем задачи и подзадачи

            manager1.addSubtask(subtask1);
            manager1.addSubtask(subtask2);

            Task task3 = new Task("Task3", "newTask3", TaskStatus.NEW,
                    Duration.ofMinutes(90), LocalDateTime.of(2025, 3, 12, 12, 0));
            manager1.addTask(task3);

            // Выводим состояние первого менеджера
            System.out.println("Первый менеджер:");
            System.out.println("Tasks: " + manager1.getTasks());
            System.out.println("Epics: " + manager1.getEpics());
            System.out.println("Subtasks: " + manager1.getSubtasks());
            System.out.println("Prioritized Tasks: " + manager1.getPrioritizedTasks());

            // Используем Optional для получения эпика
            Optional<Epic> epicFromManager1 = manager1.getEpicById(epicId);
            if (epicFromManager1.isPresent()) {
                Epic epic = epicFromManager1.get();
                System.out.println("Epic1 subtask IDs: " + epic.getSubTaskIds());
                System.out.println("Epic1 startTime: " + epic.getStartTime());
                System.out.println("Epic1 duration: " + epic.getDuration());
                System.out.println("Epic1 endTime: " + epic.getEndTime());
            } else {
                System.out.println("Эпик не найден в первом менеджере");
            }

            FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);

            // Выводим состояние второго менеджера
            System.out.println("\nВторой менеджер (загружен из файла):");
            System.out.println("Tasks: " + manager2.getTasks());
            System.out.println("Epics: " + manager2.getEpics());
            System.out.println("Subtasks: " + manager2.getSubtasks());
            System.out.println("Prioritized Tasks: " + manager2.getPrioritizedTasks());

            Optional<Epic> epicFromManager2 = manager2.getEpicById(epicId);
            if (epicFromManager2.isPresent()) {
                Epic epic = epicFromManager2.get();
                System.out.println("Epic1 subtask IDs: " + epic.getSubTaskIds());
                System.out.println("Epic1 startTime: " + epic.getStartTime());
                System.out.println("Epic1 duration: " + epic.getDuration());
                System.out.println("Epic1 endTime: " + epic.getEndTime());
            } else {
                System.out.println("Эпик не найден во втором менеджере");
            }

            // Проверки на идентичность
            assert manager1.getTasks().equals(manager2.getTasks()) : "Задачи не совпадают";
            assert manager1.getEpics().equals(manager2.getEpics()) : "Эпики не совпадают";
            assert manager1.getSubtasks().equals(manager2.getSubtasks()) : "Подзадачи не совпадают";
            assert manager1.getEpicById(epicId).get().getSubTaskIds()
                    .equals(manager2.getEpicById(epicId).get().getSubTaskIds()) :
                    "Список ID подзадач эпика не совпадает";

            System.out.println("\nВсе проверки пройдены успешно!");
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка валидации: " + e.getMessage());
        }
    }

}
