package TaskManager;

import Task.Task;
import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
}
