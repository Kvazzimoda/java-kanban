package data;

import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int id;
    private TaskStatus status; // исправил модификатор доступа

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {// добавлен
        this.title = title;
    }

    public void setDescription(String description) { //добавлен
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) { // сеттер на статус был изначально :)
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status);
    }

    @Override
    public String toString() { // Переопределение метода для адекватной печати имени класса
        return this.getClass().getSimpleName() +
                "{title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", id=" + id +
                '}' + "\n";
    }
}
