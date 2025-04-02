package httpService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.Epic;
import data.SubTask;
import data.TaskStatus;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest {
    private HttpTaskServer server;
    private HttpClient client;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpTaskServer();
        taskManager = server.getTaskManager();
        server.start();

        client = HttpClient.newHttpClient();

        taskManager.clearTask();
        taskManager.clearSubtask();
        taskManager.deleteEpic();

    }

    @AfterEach
    void tearDown() {

        server.stop();
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        String epicJson = """
                {
                    "type": "EPIC",
                    "title": "Epic 1",
                    "description": "Epic Description 1"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response body: " + response.body()); // Логируем тело ответа

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Epic created\""));
        assertTrue(response.body().contains("\"id\": 1"));

        Epic epic = taskManager.getEpicById(1).orElse(null);
        assertNotNull(epic);
        assertEquals("Epic 1", epic.getTitle());
        assertEquals("Epic Description 1", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description 1", TaskStatus.NEW, epicId,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        taskManager.addSubtask(subtask);
        int subtaskId = subtask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response body: " + response.body());

        assertEquals(200, response.statusCode());

        // Парсим JSON-ответ
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        assertEquals("Epic 1", jsonResponse.get("title").getAsString());
        JsonArray subTaskIds = jsonResponse.getAsJsonArray("subTaskIds");
        assertEquals(1, subTaskIds.size());
        assertEquals(subtaskId, subTaskIds.get(0).getAsInt());
    }

    @Test
    void testGetNonExistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("\"error\": \"Epic with ID 999 not found\""));
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        taskManager.addEpic(epic);

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description 1", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        taskManager.addSubtask(subtask);

        assertTrue(taskManager.getPrioritizedTasks().contains(subtask));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Epic deleted\""));
        assertTrue(response.body().contains("\"id\": 1"));

        assertFalse(taskManager.getEpicById(1).isPresent());
        assertFalse(taskManager.getSubTaskById(2).isPresent());
        taskManager.deleteEpicById(epic.getId());
        assertFalse(taskManager.getPrioritizedTasks().contains(subtask));
    }

    @Test
    void testDeleteNonExistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Epic deleted\""));
        assertTrue(response.body().contains("\"id\": 999"));
    }
}