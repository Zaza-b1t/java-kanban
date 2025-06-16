package taskManager;

import task.*;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setup() {
        manager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    void createAndGetTask_shouldReturnSameTask() {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.createTask(task);

        Task retrieved = manager.getTaskById(task.getId());
        assertEquals(task, retrieved);
    }

    @Test
    void createAndGetEpic_shouldReturnSameEpic() {
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1");
        manager.createEpic(epic);

        Epic retrieved = manager.getEpicById(epic.getId());
        assertEquals(epic, retrieved);
    }

    @Test
    void createAndGetSubtask_shouldReturnSameSubtask() {
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Подзадача 1.1", "Описание подзадачи 1.1", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        Subtask retrieved = manager.getSubtaskById(subtask.getId());
        assertEquals(subtask, retrieved);
    }

    @Test
    void createdTasks_shouldHaveUniqueIds() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void updateTask_shouldModifyTaskData() {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.createTask(task);

        task.setDescription("Обновленное описание задачи");
        manager.updateTask(task);

        Task updated = manager.getTaskById(task.getId());
        assertEquals("Обновленное описание задачи", updated.getDescription());
    }

    @Test
    void deletingSubtask_shouldRemoveFromEpic() {
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Подзадача 1.1", "Описание подзадачи 1.1", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        manager.deleteSubtaskById(subtask.getId());

        assertFalse(manager.getEpicById(epic.getId()).getSubtaskIds().contains(subtask.getId()));
        assertNull(manager.getSubtaskById(subtask.getId()));
    }

    @Test
    void deletingEpic_shouldRemoveAllSubtasks() {
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask(0, "Подзадача 1.1", "Описание подзадачи 1.1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask(0, "Подзадача 1.2", "Описание подзадачи 1.2", Status.NEW, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getEpicById(epic.getId()));
        assertNull(manager.getSubtaskById(subtask1.getId()));
        assertNull(manager.getSubtaskById(subtask2.getId()));
    }

    @Test
    void taskSetters_shouldNotBreakManagerDataIntegrity() {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.createTask(task);

        task.setId(999); // Changing ID manually
        // Now retrieving by old id should fail
        assertNull(manager.getTaskById(0));
        // But by new id won't work because manager doesn't know it
        assertNull(manager.getTaskById(999));
    }
}
