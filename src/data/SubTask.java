package data;

import java.util.Objects;

public class SubTask extends Task {
    private final int epicId; // ID эпика, к которому относится подзадача
    protected int subTaskId;

    public SubTask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true; // Проверка на ссылочную равность
        if (object == null || getClass() != object.getClass()) return false; // Проверка на тип
        SubTask subTask = (SubTask) object;
        return subTaskId == subTask.subTaskId; // Сравнение только по id
    }

    @Override
    public int hashCode() {
        return Objects.hash(subTaskId); // Хэш-код основан только на id
    }
}
