package Task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void taskShouldBeEqualIfIdIsEqual() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);

        assertEquals(task1, task2, "Задачи должны быть равны, если у них одинаковые id");
    }

    @Test
    void taskShouldNotBeEqualIfIdIsDifferent() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", Status.NEW);

        assertNotEquals(task1, task2, "Задачи не должны быть равны, если у них разные id");
    }
}
