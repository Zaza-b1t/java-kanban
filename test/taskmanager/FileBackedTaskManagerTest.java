package taskmanager;

import org.junit.jupiter.api.*;
import task.*;
import taskmanager.*;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    File tempFile;
    FileBackedTaskManager manager;

    @BeforeEach
    void setup() throws Exception {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task = new Task(0, "t1", "desc1", Status.NEW);
        Epic epic = new Epic(0, "e1", "desc2");
        Subtask sub = new Subtask(0, "s1", "desc3", Status.NEW, 1);
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(sub);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubtasks().size());
    }
}