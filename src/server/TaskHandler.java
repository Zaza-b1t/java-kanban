package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Task;
import taskmanager.TaskManager;
import taskmanager.TaskOverlapException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[2]);
                    Task task = taskManager.getTaskById(id);
                    if (task == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(task);
                        sendText(exchange, json);
                    }
                } else {
                    String json = gson.toJson(taskManager.getAllTasks());
                    sendText(exchange, json);
                }
                break;
            case "POST":
                try {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body,Task.class);
                    Task createTask = taskManager.createTask(task);
                    String response = gson.toJson(createTask);
                    sendCreated(exchange,response);
                    break;
                } catch (TaskOverlapException e) {
                    sendHasInteractions(exchange);
                    break;
                }
            case "DELETE":
                String paths = exchange.getRequestURI().getPath();
                String[] part = paths.split("/");
                if (part.length == 3) {
                    int id = Integer.parseInt(part[2]);
                    Task task = taskManager.getTaskById(id);
                    if (task == null) {
                        sendNotFound(exchange);
                    } else {
                        taskManager.deleteTaskById(id);
                        sendText(exchange, "Задача удалена");
                    }
                    break;
                }
            default:
                sendMethodNotAllowed(exchange);
        }
    }
}
