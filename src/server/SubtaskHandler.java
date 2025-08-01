package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Subtask;
import taskmanager.TaskManager;
import taskmanager.TaskOverlapException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private  final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                if(parts.length == 3) {
                    int id = Integer.parseInt(parts[2]);
                    Subtask subtask = taskManager.getSubtaskById(id);
                    if (subtask == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(subtask);
                        sendText(exchange, json);
                    }
                } else {
                    String json = gson.toJson(taskManager.getAllSubtasks());
                    sendText(exchange, json);
                }
                break;
            case "POST":
                try {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body,Subtask.class);
                    Subtask createSubTask = taskManager.createSubtask(subtask);
                    String response = gson.toJson(createSubTask);
                    sendCreated(exchange,response);
                    break;
                } catch (TaskOverlapException e) {
                    sendHasInteractions(exchange);
                    break;
                }
            case "DELETE":
                String paths = exchange.getRequestURI().getPath();
                String[] part = paths.split("/");
                if(part.length == 3) {
                    int id = Integer.parseInt(part[2]);
                    Subtask subtask = taskManager.getSubtaskById(id);
                    if (subtask == null) {
                        sendNotFound(exchange);
                    } else {
                        taskManager.deleteSubtaskById(id);
                        sendText(exchange, "Задача удалена");
                    }
                    break;
                }
            default:
                sendMethodNotAllowed(exchange);
        }
    }
}
