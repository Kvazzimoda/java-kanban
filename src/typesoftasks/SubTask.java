package typesoftasks;

public class SubTask extends Task {
    private final int epicId; // ID эпика, к которому относится подзадача

    public SubTask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

}
