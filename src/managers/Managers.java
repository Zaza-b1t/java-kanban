package managers;

import taskManager.TaskManager;
import taskManager.InMemoryTaskManager;
import taskManager.HistoryManager;
import taskManager.InMemoryHistoryManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
