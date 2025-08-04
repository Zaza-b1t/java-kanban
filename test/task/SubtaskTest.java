package task;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void subtaskShouldBeEqualIfIdIsEqual() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask(1, "Подзадача 1.1", "Описание подзадачи 1.1",
                Status.NEW, epic.getId(),
                Duration.ofMinutes(10), LocalDateTime.of(2023, 1, 1, 12, 0));
        Subtask subtask2 = new Subtask(1, "Подзадача 1.1", "Описание подзадачи 1.1",
                Status.NEW, epic.getId(),
                Duration.ofMinutes(10), LocalDateTime.of(2023, 1, 1, 12, 0));

        assertEquals(subtask1, subtask2, "Подзадачи должны быть равны, если у них одинаковые id");
    }

    @Test
    void subtaskShouldNotBeEqualIfIdIsDifferent() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask(1, "Подзадача 1.1", "Описание подзадачи 1.1",
                Status.NEW, epic.getId(),
                Duration.ofMinutes(10), LocalDateTime.of(2023, 1, 1, 12, 0));
        Subtask subtask2 = new Subtask(2, "Подзадача 1.2", "Описание подзадачи 1.2",
                Status.NEW, epic.getId(),
                Duration.ofMinutes(10), LocalDateTime.of(2023, 1, 1, 12, 0));

        assertNotEquals(subtask1, subtask2, "Подзадачи не должны быть равны, если у них разные id");
    }
}
