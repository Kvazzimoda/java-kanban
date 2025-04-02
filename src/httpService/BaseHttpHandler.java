package httpService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public abstract class BaseHttpHandler {
    protected final Gson gson;

    public BaseHttpHandler() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gsonBuilder.registerTypeAdapterFactory(new TaskTypeAdapterFactory());
        this.gson = gsonBuilder.setPrettyPrinting().create();
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        if (text == null) {
            text = "";
        }
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("error", message);
        sendText(exchange, gson.toJson(json), 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("message", message);
        sendText(exchange, gson.toJson(json), 400);
    }

    protected void sendInternalError(HttpExchange exchange, String message) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("error", message);
        sendText(exchange, gson.toJson(json), 500);
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        if (exchange.getRequestBody() == null) {
            return "";
        }
        try (var reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}