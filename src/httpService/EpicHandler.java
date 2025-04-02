package httpService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.Epic;
import data.TaskStatus;
import manager.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String[] pathParts = path.split("/");

        try {
            if (method.equals("GET")) {
                if (pathParts.length == 2) { // GET /epics
                    sendText(exchange, gson.toJson(manager.getEpics().values()), 200);
                } else if (pathParts.length == 3) { // GET /epics/{id}
                    int id = Integer.parseInt(pathParts[2]);
                    Optional<Epic> epic = manager.getEpicById(id);
                    if (epic.isPresent()) {
                        sendText(exchange, gson.toJson(epic.get()), 200);
                    } else {
                        sendNotFound(exchange, "Epic with ID " + id + " not found");
                    }
                } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) { // GET /epics/{id}/subtasks
                    int id = Integer.parseInt(pathParts[2]);
                    Optional<Epic> epic = manager.getEpicById(id);
                    if (epic.isPresent()) {
                        sendText(exchange, gson.toJson(manager.getSubtaskByEpic(epic.get())), 200);
                    } else {
                        sendNotFound(exchange, "Epic with ID " + id + " not found");
                    }
                } else {
                    sendNotFound(exchange, "Not Found");
                }
            } else if (method.equals("POST")) {
                String body = readRequestBody(exchange);
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                if (!jsonObject.has("type") || !jsonObject.get("type").getAsString().equals("EPIC")) {
                    sendHasInteractions(exchange, "Expected type EPIC for /epics endpoint");
                    return;
                }

                if (pathParts.length == 2) { // POST /epics (addEpic)
                    Epic epic = gson.fromJson(body, Epic.class);
                    epic.setStatus(TaskStatus.NEW); // Устанавливаем статус по умолчанию
                    manager.addEpic(epic);
                    sendText(exchange, "{\"message\": \"Epic created\", \"id\": " + epic.getId() + "}", 201);
                } else if (pathParts.length == 3) { // POST /epics/{id} (updateEpic)
                    int id = Integer.parseInt(pathParts[2]);
                    Optional<Epic> existingEpic = manager.getEpicById(id);
                    if (existingEpic.isPresent()) {
                        Epic updateData = gson.fromJson(body, Epic.class);
                        manager.updateEpic(existingEpic.get(), updateData.getTitle(), updateData.getDescription());
                        sendText(exchange, "{\"message\": \"Epic updated\", \"id\": " + id + "}", 201);
                    } else {
                        sendNotFound(exchange, "Epic with ID " + id + " not found");
                    }
                } else {
                    sendNotFound(exchange, "Not Found");
                }
            } else if (method.equals("DELETE") && pathParts.length == 3) { // DELETE /epics/{id}
                int id = Integer.parseInt(pathParts[2]);
                manager.deleteEpicById(id);
                sendText(exchange, "{\"message\": \"Epic deleted\", \"id\": " + id + "}", 200);
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