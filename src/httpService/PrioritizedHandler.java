package httpService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if (method.equals("GET")) { // GET /prioritized
                sendText(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
            } else {
                sendNotFound(exchange, "Not Found");
            }
        } catch (manager.ManagerSaveException e) {
            sendInternalError(exchange, "Failed to save tasks: " + e.getMessage());
        } catch (Exception e) {
            sendInternalError(exchange, "Internal Server Error: " + e.getMessage());
        }
    }
}