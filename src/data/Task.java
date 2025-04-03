package data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String title, String description, TaskStatus status, int id, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task() {
    }

    public int getId() {
        return id;
    }

    public TypeTask getType() {
        return TypeTask.TASK; // По умолчанию для Task
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        System.out.println("Setting ID to: " + id + " in " + this.getClass().getSimpleName());
        this.id = id;
        System.out.println("ID after set: " + this.id);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // Новый метод для вычисления времени завершения задачи
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }


    public int compareTo(Task other) {
        // Сначала сравниваем startTime
        if (this.startTime == null && other.startTime == null) {
            // Если startTime у обоих null, сравниваем по id
            return Integer.compare(this.id, other.id);
        }
        if (this.startTime == null) {
            return 1;
        }
        if (other.startTime == null) {
            return -1;
        }
        int startTimeComparison = this.startTime.compareTo(other.startTime);
        if (startTimeComparison != 0) {
            return startTimeComparison;
        }

        // Если startTime равны, сравниваем по id
        int idComparison = Integer.compare(this.id, other.id);
        if (idComparison != 0) {
            return idComparison;
        }

        // Если id равны, сравниваем по title
        if (this.title == null && other.title == null) {
            return 0;
        }
        if (this.title == null) {
            return 1;
        }
        if (other.title == null) {
            return -1;
        }
        int titleComparison = this.title.compareTo(other.title);
        if (titleComparison != 0) {
            return titleComparison;
        }

        // Если title равны, сравниваем по description
        if (this.description == null && other.description == null) {
            return 0;
        }
        if (this.description == null) {
            return 1;
        }
        if (other.description == null) {
            return -1;
        }
        int descriptionComparison = this.description.compareTo(other.description);
        if (descriptionComparison != 0) {
            return descriptionComparison;
        }

        // Если description равны, сравниваем по status
        return this.status.compareTo(other.status);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status, id, duration, startTime);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "{title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", id=" + id +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}' + "\n";
    }
}
