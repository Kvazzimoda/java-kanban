package data; // имя пакета больше не содержит заглавных букв

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subTaskIds; // теперь храним список идентификаторов задач
    private int epicId;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        this.subTaskIds = new ArrayList<>();
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubtaskId(int subTaskId) {
        if (subTaskId == getId()) {
            throw new IllegalArgumentException("Epic не может добавить себя в качестве подзадачи");
        }
        subTaskIds.add(subTaskId);
    }

    public void removeSubTaskId(int subTaskId) {
        subTaskIds.remove(Integer.valueOf(subTaskId));
    }

    public void clearSubTasks() {
        subTaskIds.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return epicId == epic.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicId);
    }
}
