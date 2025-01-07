package typesOfTasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        this.subTasks = new ArrayList<>();
    }

    public void addSubtask(SubTask subtask) {
        subTasks.add(subtask);
        updateStatus();
    }

    public void removeSubTask(SubTask subtask) {
        subTasks.remove(subtask);
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    public List<SubTask> getSubtasks() {
        return subTasks;
    }

    public void updateStatus() {
        if (subTasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subTask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            setStatus(TaskStatus.NEW);
        } else if (allDone) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }
 }
