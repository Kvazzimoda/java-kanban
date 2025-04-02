package httpService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.SubTask;
import data.TaskStatus;
import manager.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String[] pathParts = path.split("/");

        try {
            if (method.equals("GET")) {
                if (pathParts.length == 2) { // GET /subtasks
                    sendText(exchange, gson.toJson(manager.getSubtasks().values()), 200);
                } else if (pathParts.length == 3) { // GET /subtasks/{id}
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        Optional<SubTask> subtask = manager.getSubTaskById(id);
                        if (subtask.isPresent()) {
                            sendText(exchange, gson.toJson(subtask.get()), 200);
                        } else {
                            sendNotFound(exchange, "Subtask with ID " + id + " not found");
                        }
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange, "Invalid subtask ID format: " + pathParts[2]);
                    }
                } else {
                    sendNotFound(exchange, "Not Found");
                }
            } else if (method.equals("POST")) {
                String body = readRequestBody(exchange);
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                if (!jsonObject.has("type") || !jsonObject.get("type").getAsString().equals("SUBTASK")) {
                    sendHasInteractions(exchange, "Expected type SUBTASK for /subtasks endpoint");
                    return;
                }
                SubTask subtask = gson.fromJson(body, SubTask.class);
                if (pathParts.length == 2) { // POST /subtasks (addSubtask)
                    subtask.setStatus(TaskStatus.NEW);
                    System.out.println("Subtask epicId: " + subtask.getEpicId());
                    manager.addSubtask(subtask);
                    System.out.println("Subtask created with id: " + subtask.getId());
                    sendText(exchange, "{\"message\": \"Subtask created\", \"id\": " + subtask.getId() + "}", 201);
                } else if (pathParts.length == 3) { // POST /subtasks/{id} (updateSubTask)
                    int id = Integer.parseInt(pathParts[2]);
                    Optional<SubTask> existingSubtask = manager.getSubTaskById(id);
                    if (existingSubtask.isPresent()) {
                        manager.updateSubTask(existingSubtask.get(), subtask);
                        sendText(exchange, "{\"message\": \"Subtask updated\", \"id\": " + id + "}", 201);
                    } else {
                        sendNotFound(exchange, "Subtask with ID " + id + " not found");
                    }
                } else {
                    sendNotFound(exchange, "Not Found");
                }
            } else if (method.equals("DELETE") && pathParts.length == 3) { // DELETE /subtasks/{id}
                int id = Integer.parseInt(pathParts[2]);
                manager.deleteSubTaskById(id);
                sendText(exchange, "{\"message\": \"Subtask deleted\", \"id\": " + id + "}", 200);
            } else {
                sendNotFound(exchange, "Not Found");
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