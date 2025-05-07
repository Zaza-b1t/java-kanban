package TaskManager;

import Task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldSaveTaskHistory() {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        taskManager.createTask(task);

        historyManager.add(task);

        assertTrue(historyManager.getHistory().contains(task), "История должна содержать задачу");
    }

    @Test
    void shouldMaintainTaskHistoryWhenLimitReached() {
        for (int i = 1; i <= 12; i++) {
            Task task = new Task(0, "Задача " + i, "Описание задачи " + i, Status.NEW);
            taskManager.createTask(task);
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size(), "История должна содержать не более 10 задач");
    }
}
