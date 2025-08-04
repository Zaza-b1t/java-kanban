package httpserver;

import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerHistoryTest extends BaseHttpTest {

    public HttpTaskManagerHistoryTest() {
    }

    @Test
    public void testGetHistory() throws Exception {
        Task task = new Task(0, "Task for history", "desc", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson = gson.toJson(task);
        sendPost(TASKS_URI,taskJson);

        int id = manager.getAllTasks().getFirst().getId();
        manager.getTaskById(id);

        HttpResponse<String> response = sendGet(HISTORY_URI);

        assertEquals(HttpStatus.OK.getCode(), response.statusCode());

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, history.length);
        assertEquals("Task for history", history[0].getTitle());
    }
}
