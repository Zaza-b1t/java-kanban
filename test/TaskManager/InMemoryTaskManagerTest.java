package TaskManager;

import Task.*;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    void shouldAddTaskAndRetrieveById() {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.createTask(task);

        Task retrievedTask = manager.getTaskById(task.getId());

        assertEquals(task, retrievedTask, "Задача должна быть добавлена и найдена по id");
    }

    @Test
    void shouldAddEpicAndRetrieveById() {
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1");
        manager.createEpic(epic);

        Epic retrievedEpic = manager.getEpicById(epic.getId());

        assertEquals(epic, retrievedEpic, "Эпик должен быть добавлен и найден по id");
    }

    @Test
    void shouldAddSubtaskAndRetrieveById() {
        Epic epic = new Epic(0, "Эпик 1", "Описание эпика 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Подзадача 1.1", "Описание подзадачи 1.1", Status.NEW, epic.getId());
        manager.createSubtask(subtask);

        Subtask retrievedSubtask = manager.getSubtaskById(subtask.getId());

        assertEquals(subtask, retrievedSubtask, "Подзадача должна быть добавлена и найдена по id");
    }

    @Test
    void taskShouldNotConflictWithGeneratedId() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId(), "Задачи не должны иметь одинаковые id, если они добавлены разными");
    }

    @Test
    void shouldPreserveTaskDataWhenUpdated() {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        manager.createTask(task);

        task.setDescription("Обновленное описание задачи");
        manager.updateTask(task);

        Task updatedTask = manager.getTaskById(task.getId());

        assertEquals("Обновленное описание задачи", updatedTask.getDescription(), "Задача должна быть обновлена с правильными данными");
    }
}
