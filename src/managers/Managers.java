package managers;

import taskmanager.TaskManager;
import taskmanager.InMemoryTaskManager;
import taskmanager.HistoryManager;
import taskmanager.InMemoryHistoryManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
