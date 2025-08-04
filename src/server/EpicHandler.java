package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Epic;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = GsonConfig.GSON;

    public EpicHandler(TaskManager taskManager) {
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
                    Epic epic = taskManager.getEpicById(id);
                    if (epic == null) {
                        sendNotFound(exchange);
                    } else {
                        String json = gson.toJson(epic);
                        sendText(exchange, json);
                    }
                } else {
                    String json = gson.toJson(taskManager.getAllEpics());
                    sendText(exchange, json);
                }
                break;
            case "POST":
                try {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);
                    Epic createEpic = taskManager.createEpic(epic);
                    String response = gson.toJson(createEpic);
                    sendCreated(exchange,response);
                    break;
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, 0);
                    exchange.close();
                }
                break;
            case "DELETE":
                String paths = exchange.getRequestURI().getPath();
                String[] part = paths.split("/");
                if (part.length == 3) {
                    int id = Integer.parseInt(part[2]);
                    Epic epic = taskManager.getEpicById(id);
                    if (epic == null) {
                        sendNotFound(exchange);
                    } else {
                        taskManager.deleteEpicById(id);
                        sendText(exchange, "Задача удалена");
                    }
                    break;
                }
            default:
                sendMethodNotAllowed(exchange);
        }

    }
}
