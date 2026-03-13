package cn.kurt6.chunkmonitor.integration.discord.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public record WebhookEmbed(Integer color, String title, String description, List<EmbedField> fields) {
    public JsonObject toJson() {
        if(title == null || title.length() > 256) return null;

        JsonObject json = new JsonObject();

        if(color != null)
            json.addProperty("color", color);

        json.addProperty("title", title);

        if(description != null && description.length() < 4096)
            json.addProperty("description", description);

        if(fields != null && !fields.isEmpty()) {
            JsonArray fieldArray = new JsonArray();
            int size = fields.size();
            size = Math.min(size, 25);
            for (int i = 0; i < size; i++) {
                var field = fields.get(i);
                var fieldJson = field.toJson();
                if (fieldJson != null)
                    fieldArray.add(fieldJson);
            }
            if(!fieldArray.isEmpty())
                json.add("fields", fieldArray);
        }

        return json;
    }

    public record EmbedField(String name, String value) {
        public JsonObject toJson() {
            if(name == null || name.length() > 256) return null;

            JsonObject json = new JsonObject();

            json.addProperty("name", name);

            if(value != null && value.length() < 1024)
                json.addProperty("value", value);

            return json;
        }
    }
}