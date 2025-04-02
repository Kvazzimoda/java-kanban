package httpService;

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

public class SubtaskHandlerTest {
    private HttpTaskServer server;
    private TaskManager taskManager;
    private HttpClient client;

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
    void testGetAllSubtasks() throws IOException, InterruptedException {
        // Добавляем эпик
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        // Добавляем подзадачу
        SubTask subtask = new SubTask("Subtask 1", "Subtask Description 1", TaskStatus.NEW, epicId,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        taskManager.addSubtask(subtask);

        // Запрос GET /subtasks
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"title\": \"Subtask 1\""));
        assertTrue(response.body().contains("\"description\": \"Subtask Description 1\""));
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        // Добавляем эпик
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        // Добавляем подзадачу
        SubTask subtask = new SubTask("Subtask 1", "Subtask Description 1", TaskStatus.NEW, epicId,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        taskManager.addSubtask(subtask);
        int subtaskId = subtask.getId();

        // Запрос GET /subtasks/{id}
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"title\": \"Subtask 1\""));
        assertTrue(response.body().contains("\"description\": \"Subtask Description 1\""));
        assertTrue(response.body().contains("\"id\": " + subtaskId));
    }

    @Test
    void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        // Запрос GET /subtasks/{id} для несуществующей подзадачи
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("\"error\": \"Subtask with ID 999 not found\""));
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        // Добавляем эпик
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        // JSON для новой подзадачи
        String subtaskJson = String.format("""
                {
                    "type": "SUBTASK",
                    "title": "Subtask 1",
                    "description": "Subtask Description 1",
                    "status": "NEW",
                    "epicId": %d,
                    "duration": 60,
                    "startTime": "13-03-2025 05:00:00"
                }
                """, epicId);

        // Запрос POST /subtasks
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Парсим JSON-ответ
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        int subtaskId = jsonResponse.get("id").getAsInt();

        assertEquals(201, response.statusCode());
        assertEquals("Subtask created", jsonResponse.get("message").getAsString());

        SubTask subtask = taskManager.getSubTaskById(subtaskId).orElse(null);
        assertNotNull(subtask);
        assertEquals("Subtask 1", subtask.getTitle());
        assertEquals("Subtask Description 1", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(epicId, subtask.getEpicId());
    }

    @Test
    void testAddSubtaskInvalidType() throws IOException, InterruptedException {
        // JSON с неправильным типом
        String subtaskJson = """
                {
                    "type": "TASK",
                    "title": "Subtask 1",
                    "description": "Subtask Description 1"
                }
                """;

        // Запрос POST /subtasks
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Expected type SUBTASK for /subtasks endpoint\""));
    }

    @Test
    void testAddSubtaskInvalidEpicId() throws IOException, InterruptedException {
        // JSON с несуществующим epicId
        String subtaskJson = """
                {
                    "type": "SUBTASK",
                    "title": "Subtask 1",
                    "description": "Subtask Description 1",
                    "status": "NEW",
                    "epicId": 999,
                    "duration": 60,
                    "startTime": "13-03-2025 16:00:00"
                }
                """;

        // Запрос POST /subtasks
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Epic with ID 999 not found\""));
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        // Добавляем эпик
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        // Добавляем подзадачу
        SubTask subtask = new SubTask("Subtask 1", "Subtask Description 1", TaskStatus.NEW, epicId,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        taskManager.addSubtask(subtask);
        int subtaskId = subtask.getId();

        // JSON для обновления подзадачи
        String updatedSubtaskJson = """
                {
                    "type": "SUBTASK",
                    "title": "Updated Subtask",
                    "description": "Updated Description",
                    "status": "IN_PROGRESS",
                    "epicId": %d,
                    "duration": 120,
                    "startTime": "13-03-2025 06:00:00"
                }
                """.formatted(epicId);

        // Запрос POST /subtasks/{id}
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Subtask updated\""));
        assertTrue(response.body().contains("\"id\": " + subtaskId));

        SubTask updatedSubtask = taskManager.getSubTaskById(subtaskId).orElse(null);
        assertNotNull(updatedSubtask);
        assertEquals("Updated Subtask", updatedSubtask.getTitle());
        assertEquals("Updated Description", updatedSubtask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedSubtask.getStatus());
        assertEquals(Duration.ofMinutes(120), updatedSubtask.getDuration());
        assertEquals(LocalDateTime.of(2025, 3, 13, 6, 0), updatedSubtask.getStartTime());
    }

    @Test
    void testUpdateSubtaskNotFound() throws IOException, InterruptedException {
        // JSON для обновления подзадачи
        String updatedSubtaskJson = """
                {
                    "type": "SUBTASK",
                    "title": "Updated Subtask",
                    "description": "Updated Description"
                }
                """;

        // Запрос POST /subtasks/{id} для несуществующей подзадачи
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("\"error\": \"Subtask with ID 999 not found\""));
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        // Добавляем эпик
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        // Добавляем подзадачу
        SubTask subtask = new SubTask("Subtask 1", "Subtask Description 1", TaskStatus.NEW, epicId,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        taskManager.addSubtask(subtask);
        int subtaskId = subtask.getId();

        // Запрос DELETE /subtasks/{id}
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Subtask deleted\""));
        assertTrue(response.body().contains("\"id\": " + subtaskId));

        assertFalse(taskManager.getSubTaskById(subtaskId).isPresent());
        assertFalse(taskManager.getPrioritizedTasks().contains(subtask));
        assertFalse(epic.getSubTaskIds().contains(subtaskId));
    }

    @Test
    void testInvalidPath() throws IOException, InterruptedException {
        // Запрос GET /subtasks/invalid
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/invalid"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("\"error\": \"Invalid subtask ID format: invalid\""));
    }
}