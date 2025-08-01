package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.DurationAdapter;
import server.HttpTaskServer;
import server.LocalDateTimeAdapter;
import task.Epic;
import task.Status;
import task.Subtask;
import taskmanager.InMemoryTaskManager;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubTasksTest {
    TaskManager manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskManagerSubTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Epic for subtask", "Epic desc");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI epicUrl = URI.create("http://localhost:8080/epics");
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(epicUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        int epicId = manager.getAllEpics().getFirst().getId();

        Subtask subtask = new Subtask(2, "Test 1", "Описание подзадачи 1.2",
                Status.NEW, epicId,
                Duration.ofMinutes(10), LocalDateTime.of(2025, 1, 1, 12, 0));

        String taskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 1", subtasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetAllSubTasks() throws Exception {
        Epic epic = new Epic(0, "Epic for subtask", "Epic desc");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI epicUrl = URI.create("http://localhost:8080/epics");
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(epicUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        int epicId = manager.getAllEpics().getFirst().getId();

        Subtask subtask = new Subtask(2, "Test 2", "Описание подзадачи 1.3",
                Status.NEW, epicId,
                Duration.ofMinutes(15), LocalDateTime.of(2025, 2, 1, 12, 0));

        String taskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());

        Subtask[] subtasks = gson.fromJson(getResponse.body(), Subtask[].class);
        assertEquals(1, subtasks.length);
        assertEquals("Test 2", subtasks[0].getTitle());
    }

    @Test
    public void testDeleteSubTask() throws Exception {
        Epic epic = new Epic(0, "Epic for subtask", "Epic desc");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI epicUrl = URI.create("http://localhost:8080/epics");
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(epicUrl)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        int epicId = manager.getAllEpics().getFirst().getId();

        Subtask subtask = new Subtask(2, "Test delete", "Описание подзадачи 1.4",
                Status.NEW, epicId,
                Duration.ofMinutes(15), LocalDateTime.of(2025, 2, 1, 12, 0));

        String taskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        int id = manager.getAllSubtasks().getFirst().getId();

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + id))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode());
        assertEquals(0, manager.getAllSubtasks().size());
    }
}
