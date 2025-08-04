package httpserver;

import com.google.gson.Gson;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.GsonConfig;
import server.HttpTaskServer;
import taskmanager.InMemoryTaskManager;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class BaseHttpTest {
    protected TaskManager manager;
    protected HttpTaskServer taskServer;
    protected Gson gson;
    private final HttpClient client = HttpClient.newHttpClient();
    protected static final URI TASKS_URI = URI.create("http://localhost:8080/tasks");
    protected static final URI EPICS_URI = URI.create("http://localhost:8080/epics");
    protected static final URI SUBTASKS_URI = URI.create("http://localhost:8080/subtasks");
    protected static final URI HISTORY_URI = URI.create("http://localhost:8080/history");
    protected static final URI PRIORITIZED_URI = URI.create("http://localhost:8080/prioritized");

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
        taskServer = new HttpTaskServer(manager);
        gson = GsonConfig.GSON;

        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    protected HttpResponse<String> sendPost(URI url, String json) throws IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(postRequest, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> sendGet(URI url) throws IOException, InterruptedException {
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        return client.send(getRequest, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> sendDelete(URI url, int id) throws IOException, InterruptedException {
        URI deleteUri = URI.create(url.toString() + "/" + id);
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(deleteUri)
                .DELETE()
                .build();
        return client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
    }
}
