package httpservice;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.Task;
import manager.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String[] pathParts = path.split("/");

        try {
            if (method.equals("GET")) {
                if (pathParts.length == 2) { // GET /tasks
                    sendText(exchange, gson.toJson(manager.getTasks().values()), 200);
                } else if (pathParts.length == 3) { // GET /tasks/{id}
                    int id = Integer.parseInt(pathParts[2]);
                    Optional<Task> task = manager.getTaskById(id);
                    if (task.isPresent()) {
                        sendText(exchange, gson.toJson(task.get()), 200);
                    } else {
                        sendNotFound(exchange, "Task with ID " + id + " not found");
                    }
                } else {
                    sendNotFound(exchange, "Not Found");
                }
            } else if (method.equals("POST")) {
                String body = readRequestBody(exchange);
                System.out.println("Received JSON: " + body);
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                if (!jsonObject.has("type") || !jsonObject.get("type").getAsString().equals("TASK")) {
                    sendHasInteractions(exchange, "Expected type TASK for /tasks endpoint");
                    return;
                }

                Task task;
                try {
                    task = gson.fromJson(body, Task.class);
                    System.out.println("Parsed task: " + task);
                } catch (JsonParseException e) {
                    System.out.println("JsonParseException: " + e.getMessage());
                    throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage());
                }

                if (pathParts.length == 2) { // POST /tasks (addTask)
                    if (task.getStatus() == null) {
                        throw new IllegalArgumentException("Task status cannot be null or invalid");
                    }
                    manager.addTask(task);
                    sendText(exchange, "{\"message\": \"Task created\", \"id\": " + task.getId() + "}", 201);
                } else if (pathParts.length == 3) { // POST /tasks/{id} (updateTask)
                    int id = Integer.parseInt(pathParts[2]);
                    Optional<Task> existingTask = manager.getTaskById(id);
                    if (existingTask.isPresent()) {
                        manager.updateTask(existingTask.get(), task);
                        sendText(exchange, "{\"message\": \"Task updated\", \"id\": " + id + "}", 201);
                    } else {
                        sendNotFound(exchange, "Task with ID " + id + " not found");
                    }
                } else {
                    sendNotFound(exchange, "Not Found");
                }
            } else if (method.equals("DELETE") && pathParts.length == 3) { // DELETE /tasks/{id}
                int id = Integer.parseInt(pathParts[2]);
                Optional<Task> task = manager.getTaskById(id);
                if (task.isPresent()) {
                    manager.deleteTaskById(id);
                    sendText(exchange, "{\"message\": \"Task deleted\", \"id\": " + id + "}", 200);
                } else {
                    sendNotFound(exchange, "Task with ID " + id + " not found");
                }
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (manager.ManagerSaveException e) {
            sendInternalError(exchange, "Failed to save tasks: " + e.getMessage());
        } catch (Exception e) {
            sendInternalError(exchange, "Internal Server Error: " + e.getMessage());
        }
    }
}