package managers;

import TaskManager.TaskManager;
import TaskManager.InMemoryTaskManager;
import TaskManager.HistoryManager;
import TaskManager.InMemoryHistoryManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
