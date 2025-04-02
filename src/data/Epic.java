package data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTaskIds;
    protected LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW, null, null); // duration и startTime пока null
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(int id, String title, String description, TaskStatus status) {
        super(title, description, status, id, null, null); // duration и startTime пока null
        this.subTaskIds = new ArrayList<>();
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    public List<Integer> getSubTaskIds() {
        if (subTaskIds == null) {
            subTaskIds = new ArrayList<>();
        }
        return subTaskIds;
    }

    public void setTitle(String title) {
        if ((title == null) || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым или null");
        }
        this.title = title;
    }

    public void addSubtaskId(int subTaskId) {
        if (subTaskId == getId()) {
            throw new IllegalArgumentException("Epic не может добавить себя в качестве подзадачи");
        }
        getSubTaskIds().add(subTaskId);
    }

    public void removeSubTaskId(int subTaskId) {
        subTaskIds.remove(Integer.valueOf(subTaskId));
    }

    public void clearSubTasks() {
        subTaskIds.clear();
    }

    @Override
    public Duration getDuration() {
        return super.getDuration();
    }


    @Override
    public void setStartTime(LocalDateTime startTime) {
        System.out.println("Установка startTime для эпика " + title + ": " + startTime);
        super.setStartTime(startTime);
    }

    public void setEndTime(LocalDateTime endTime) {
        System.out.println("Установка endTime для эпика " + title + ": " + endTime);
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return id == epic.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
