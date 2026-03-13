package cn.kurt6.chunkmonitor.integration.discord.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public record WebhookMessage(String username, String avatarUrl, String content, List<WebhookEmbed> embeds,
                             boolean suppressNotifications, String threadName) {
    public String toJson() {
        if(content == null && (embeds == null || embeds.isEmpty()))
            return null;

        JsonObject json = new JsonObject();

        if(content != null)
            json.addProperty("content", content);

        if (embeds != null && !embeds.isEmpty()) {
            JsonArray embedsArray = new JsonArray();
            for (var embed : embeds) embedsArray.add(embed.toJson());
            if(!embedsArray.isEmpty())
                json.add("embeds", embedsArray);
        }

        if(json.isEmpty())
            return null;

        if (suppressNotifications)
            json.addProperty("flags", 4096);

        if (username != null)
            json.addProperty("username", username);

        if (avatarUrl != null)
            json.addProperty("avatar_url", avatarUrl);

        if (threadName != null)
            json.addProperty("thread_name", threadName);

        return json.toString();
    }
}