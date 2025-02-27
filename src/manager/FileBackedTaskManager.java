package manager;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

import data.*;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,title,status,description,epic\n");

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

        if (task instanceof SubTask) {
            sb.append(TypeTask.SUBTASK).append(",");
        } else if (task instanceof Epic) {
            sb.append(TypeTask.EPIC).append(",");
        } else {
            sb.append(TypeTask.TASK).append(",");
        }

        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");

        // Для подзадач — ID эпика, для задач и эпиков — их имя
        if (task instanceof SubTask subtask) {
            sb.append(subtask.getEpicId()); // ID эпика для подзадач
        } else {
            sb.append(task.getTitle()); // Для задач и эпиков — их имя
        }

        return sb.toString();
    }

    private Task fromString(String value) {
        String[] parts = value.split(",", 6);
        if (parts.length < 5) {
            throw new IllegalArgumentException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        TypeTask type = TypeTask.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        String epicValue = parts.length > 5 ? parts[5].trim() : null; // Числовой ID эпика для подзадач

        return switch (type) {
            case TASK -> new Task(id, title, description, status);
            case EPIC -> new Epic(id, title, description, status.toString());
            case SUBTASK -> {
                if (epicValue == null || epicValue.isEmpty()) {
                    throw new IllegalArgumentException("Для подзадачи не указан ID эпика: " + value);
                }
                int epicId = Integer.parseInt(epicValue); // Преобразуем epicValue в числовой ID эпика
                Epic epic = getEpics().get(epicId); // Поиск эпика по ID
                if (epic == null) {
                    throw new IllegalArgumentException("Эпик с ID " + epicId + " не найден: " + value);
                }
                yield new SubTask(id, title, description, status, epicId);
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
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    Task task = manager.fromString(line);
                    System.out.println("Загружаем задачу: " + task);
                    if (!(task instanceof Epic || task instanceof SubTask)) {
                        manager.getTasks().put(task.getId(), task);
                    } else if (task instanceof Epic epic) {
                        manager.getEpics().put(epic.getId(), epic);
                    } else {
                        SubTask subTask = (SubTask) task;
                        manager.getSubtasks().put(subTask.getId(), subTask);
                        // Обновляем список подзадач в эпике
                        Epic epic = manager.getEpics().get(subTask.getEpicId());
                        if (epic != null) {
                            epic.addSubtaskId(subTask.getId());
                            manager.updateEpicStatus(epic); // Обновляем статус эпика
                        }
                    }
                }
            }

            System.out.println("Содержимое файла после загрузки:");
            for (String fileLine : lines) {
                System.out.println(fileLine);
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
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
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

            // Создаём задачи
            Task task1 = new Task("Task1", "Description1", TaskStatus.NEW);
            Task task2 = new Task("Task2", "Description2", TaskStatus.IN_PROGRESS);
            Epic epic1 = new Epic("Epic1", "Epic Description1");
            manager1.addTask(task1);
            manager1.addTask(task2);
            // Сначала добавляем эпик, чтобы получить его ID
            manager1.addEpic(epic1);
            int epicId = epic1.getId(); // Получаем сгенерированный ID после добавления

            SubTask subtask1 = new SubTask("SubTask1", "SubTask Description1", TaskStatus.DONE, epicId);
            SubTask subtask2 = new SubTask("SubTask2", "SubTask Description2", TaskStatus.NEW, epicId);

            // Добавляем задачи и подзадачи

            manager1.addSubtask(subtask1);
            manager1.addSubtask(subtask2);

            manager1.deleteTaskById(1);

            Task task3 = new Task("Task3", "newTask3", TaskStatus.NEW);
            manager1.addTask(task3);

            System.out.println("Первый менеджер:");
            System.out.println("Tasks: " + manager1.getTasks());
            System.out.println("Epics: " + manager1.getEpics());
            System.out.println("Subtasks: " + manager1.getSubtasks());
            Epic epicFromManager1 = manager1.getEpics().get(epicId);
            if (epicFromManager1 != null) {
                System.out.println("Epic1 subtask IDs: " + epicFromManager1.getSubTaskIds());
            } else {
                System.out.println("Эпик не найден в первом менеджере");
            }

            FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);

            System.out.println("\nВторой менеджер (загружен из файла):");
            System.out.println("Tasks: " + manager2.getTasks());
            System.out.println("Epics: " + manager2.getEpics());
            System.out.println("Subtasks: " + manager2.getSubtasks());
            Epic epicFromManager2 = manager2.getEpics().get(epicId);
            if (epicFromManager2 != null) {
                System.out.println("Epic1 subtask IDs: " + epicFromManager2.getSubTaskIds());
            } else {
                System.out.println("Эпик не найден во втором менеджере");
            }

            assert manager1.getTasks().equals(manager2.getTasks()) : "Задачи не совпадают";
            assert manager1.getEpics().equals(manager2.getEpics()) : "Эпики не совпадают";
            assert manager1.getSubtasks().equals(manager2.getSubtasks()) : "Подзадачи не совпадают";
            assert manager1.getEpics().get(epicId).getSubTaskIds().equals(manager2.getEpics().get(epicId).getSubTaskIds()) :
                    "Список ID подзадач эпика не совпадает";

            System.out.println("\nВсе проверки пройдены успешно!");
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлом: " + e.getMessage());
        }
    }

}
