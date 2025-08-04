package httpserver;

import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerPrioritizedTest extends BaseHttpTest {

    public HttpTaskManagerPrioritizedTest() {}

    @Test
    public void testGetPrioritizedTasks() throws Exception {
        Task task1 = new Task(0, "T1", "desc", Status.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2025, 1, 1, 10, 0));
        Task task2 = new Task(0, "T2", "desc", Status.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2025, 1, 1, 9, 0));

        String taskJson1 = gson.toJson(task1);
        sendPost(TASKS_URI, taskJson1);

        String taskJson2 = gson.toJson(task2);
        sendPost(TASKS_URI, taskJson2);

        HttpResponse<String> response = sendGet(PRIORITIZED_URI);

        assertEquals(HttpStatus.OK.getCode(), response.statusCode());

        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, prioritized.length);
        assertEquals("T2", prioritized[0].getTitle());
        assertEquals("T1", prioritized[1].getTitle());
    }
}
