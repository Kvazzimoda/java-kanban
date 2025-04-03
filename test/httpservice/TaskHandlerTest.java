package httpservice;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.Task;
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

public class TaskHandlerTest {
    private HttpTaskServer server;
    private HttpClient client;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpTaskServer();
        taskManager = server.getTaskManager();
        server.start();

        client = HttpClient.newHttpClient();

        // Очищаем данные перед каждым тестом
        taskManager.clearTask();
        taskManager.clearSubtask();
        taskManager.deleteEpic();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        String taskJson = """
                {
                    "type": "TASK",
                    "title": "Task 1",
                    "description": "Task Description 1",
                    "status": "NEW",
                    "duration": 60,
                    "startTime": "13-03-2025 05:00:00"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        // Парсим id из тела ответа
        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        int taskId = responseJson.get("id").getAsInt();

        Task task = taskManager.getTaskById(taskId).orElse(null);

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Task created\""));
        assertNotNull(task);
        assertEquals("Task 1", task.getTitle());
        assertEquals("Task Description 1", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(Duration.ofMinutes(60), task.getDuration());
        assertEquals(LocalDateTime.of(2025, 3, 13, 5, 0), task.getStartTime());
    }

    @Test
    void testAddTaskWithInvalidJson() throws IOException, InterruptedException {
        String invalidJson = """
                {
                    "type": "TASK",
                    "title": "Task 1",
                    "description": "Task Description 1",
                    "status": "INVALID_STATUS",
                    "duration": 60,
                    "startTime": "13-03-2025 05:00:00"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("\"Task status cannot be null or invalid\""));
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Task Description 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        taskManager.addTask(task);
        int taskId = task.getId();

        String updatedTaskJson = """
                {
                    "type": "TASK",
                    "title": "Updated Task 1",
                    "description": "Updated Description",
                    "status": "IN_PROGRESS",
                    "duration": 90,
                    "startTime": "13-03-2025 05:00:00"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Task updated\""));

        Task updatedTask = taskManager.getTaskById(taskId).orElse(null);
        assertNotNull(updatedTask);
        assertEquals("Updated Task 1", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        assertEquals(Duration.ofMinutes(90), updatedTask.getDuration());
        assertEquals(LocalDateTime.of(2025, 3, 13, 5, 0), updatedTask.getStartTime());
    }

    @Test
    void testUpdateNonExistentTask() throws IOException, InterruptedException {
        String updatedTaskJson = """
                {
                    "type": "TASK",
                    "title": "Updated Task",
                    "description": "Updated Description",
                    "status": "IN_PROGRESS",
                    "duration": 90,
                    "startTime": "13-03-2025 05:00:00"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("\"error\": \"Task with ID 999 not found\""));
    }

    @Test
    void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Task Description 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        taskManager.addTask(task);
        int taskId = task.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"title\": \"Task 1\""));
        assertTrue(response.body().contains("\"description\": \"Task Description 1\""));
    }

    @Test
    void testGetNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("\"error\": \"Task with ID 999 not found\""));
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Task Description 1", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 13, 5, 0));
        taskManager.addTask(task);
        int taskId = task.getId();

        assertTrue(taskManager.getPrioritizedTasks().contains(task));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"message\": \"Task deleted\""));
        assertTrue(response.body().contains("\"id\": " + taskId));

        assertFalse(taskManager.getTaskById(taskId).isPresent());
        assertFalse(taskManager.getPrioritizedTasks().contains(task));
    }

    @Test
    void testDeleteNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("\"error\": \"Task with ID 999 not found\""));
    }
}