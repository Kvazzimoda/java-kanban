package data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private int epicId;
    protected int subTaskId;

    public SubTask(String title, String description, TaskStatus status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(int id, String title, String description, TaskStatus status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(title, description, status, id, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK; // Для SubTask
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true; // Проверка на ссылочную равность
        if (object == null || getClass() != object.getClass()) return false; // Проверка на тип
        SubTask subTask = (SubTask) object;
        return subTaskId == subTask.subTaskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subTaskId);
    }
}
