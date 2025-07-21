package task;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicShouldBeEqualIfIdIsEqual() {
        Epic epic1 = new Epic(1, "Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic(1, "Эпик 1", "Описание эпика 1");

        assertEquals(epic1, epic2, "Эпики должны быть равны, если у них одинаковые id");
    }

    @Test
    void epicCannotAddItselfAsSubtask() {
        Epic epic = new Epic(1, "Эпик 1", "Описание эпика 1");

        Subtask subtask = new Subtask(
                0, "Подзадача 1.1", "Описание подзадачи 1.1", Status.NEW, epic.getId(),
                Duration.ofMinutes(10), LocalDateTime.of(2023, 1, 1, 12, 0)
        );

        assertNotEquals(epic.getId(), subtask.getId(), "Эпик не может быть подзадачей самого себя");
    }
}
