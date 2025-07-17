package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    abstract T createManager();

    @BeforeEach
    void setup() {
        manager = createManager();
    }

    @Test
    void createAndGetTask_shouldReturnSameTask() {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
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
        Subtask subtask = new Subtask(0, "Подзадача", "Описание", Status.NEW, epic.getId(), Duration.ofMinutes(15), LocalDateTime.now());
        manager.createSubtask(subtask);
        Subtask retrieved = manager.getSubtaskById(subtask.getId());
        assertEquals(subtask, retrieved);
    }

    @Test
    void getPrioritizedTasks_shouldReturnSortedByStartTime() {
        Task task1 = new Task(0, "T1", "desc", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2023, 1, 1, 10, 0));
        Task task2 = new Task(0, "T2", "desc", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2023, 1, 1, 9, 0));
        manager.createTask(task1);
        manager.createTask(task2);
        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(task2, prioritized.get(0));
        assertEquals(task1, prioritized.get(1));
    }

    @Test
    void createTask_overlap_shouldThrow() {
        Task task1 = new Task(0, "T1", "desc", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2023, 1, 1, 9, 0));
        Task task2 = new Task(0, "T2", "desc", Status.NEW, Duration.ofMinutes(20), LocalDateTime.of(2023, 1, 1, 9, 15));
        manager.createTask(task1);
        assertThrows(RuntimeException.class, () -> manager.createTask(task2));
    }

    @Test
    void epicStatus_allNew_allDone_inProgress() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        manager.createEpic(epic);

        Subtask sub1 = new Subtask(0, "Sub1", "desc", Status.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.of(2023,1,1,10,0));
        Subtask sub2 = new Subtask(0, "Sub2", "desc", Status.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.of(2023,1,1,11,0));
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());

        sub1.setStatus(Status.NEW);
        sub2.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());

        sub1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(sub1);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }
}
