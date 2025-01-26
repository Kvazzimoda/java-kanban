package manager;

import data.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private final int MAX_SIZE_HISTORY = 10;

    List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= MAX_SIZE_HISTORY) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }
}
