package httpservice;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import data.*;

import java.io.IOException;

public class TaskTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Task.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        final TypeAdapter<Task> taskAdapter = gson.getDelegateAdapter(this, TypeToken.get(Task.class));
        final TypeAdapter<SubTask> subTaskAdapter = gson.getDelegateAdapter(this, TypeToken.get(SubTask.class));
        final TypeAdapter<Epic> epicAdapter = gson.getDelegateAdapter(this, TypeToken.get(Epic.class));
        final TypeAdapter<JsonElement> jsonElementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }

                JsonObject jsonObject;
                if (value instanceof Epic) {
                    jsonObject = epicAdapter.toJsonTree((Epic) value).getAsJsonObject();
                } else if (value instanceof SubTask) {
                    jsonObject = subTaskAdapter.toJsonTree((SubTask) value).getAsJsonObject();
                } else {
                    jsonObject = taskAdapter.toJsonTree((Task) value).getAsJsonObject();
                }

                // Добавляем поле type
                jsonObject.addProperty("type", value.getClass().getSimpleName().toUpperCase());

                // Сериализуем итоговый объект
                gson.getAdapter(JsonElement.class).write(out, jsonObject);
            }

            @SuppressWarnings("unchecked")
            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement jsonElement = jsonElementAdapter.read(in);
                if (!jsonElement.isJsonObject()) {
                    throw new IOException("Expected a JSON object");
                }

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (!jsonObject.has("type")) {
                    throw new IOException("Missing 'type' field in JSON");
                }

                String type = jsonObject.get("type").getAsString();
                T result;

                result = switch (type) {
                    case "TASK" -> (T) taskAdapter.fromJsonTree(jsonObject);
                    case "SUBTASK" -> (T) subTaskAdapter.fromJsonTree(jsonObject);
                    case "EPIC" -> (T) epicAdapter.fromJsonTree(jsonObject);
                    default -> throw new IOException("Unknown type: " + type);
                };

                return result;
            }
        };
    }
}