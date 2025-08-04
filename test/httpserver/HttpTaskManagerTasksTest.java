package httpserver;

import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest extends BaseHttpTest{

    public HttpTaskManagerTasksTest() {
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(0,"Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson = gson.toJson(task);
        assertEquals(HttpStatus.CREATED.getCode(), sendPost(TASKS_URI,taskJson).statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllTasks() throws Exception {
        Task task = new Task(0, "Test 3", "Get test", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        sendPost(TASKS_URI,taskJson);

        HttpResponse<String> getResponse = sendGet(TASKS_URI);

        assertEquals(HttpStatus.OK.getCode(), getResponse.statusCode());

        Task[] tasks = gson.fromJson(getResponse.body(), Task[].class);
        assertEquals(1, tasks.length);
        assertEquals("Test 3", tasks[0].getTitle());
    }

    @Test
    public void testDeleteTask() throws Exception {
        Task task = new Task(0, "Test delete", "Desc", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        sendPost(TASKS_URI,taskJson);

        int id = manager.getAllTasks().getFirst().getId();

        assertEquals(HttpStatus.OK.getCode(), sendDelete(TASKS_URI,id).statusCode());
        assertEquals(0, manager.getAllTasks().size());
    }
}