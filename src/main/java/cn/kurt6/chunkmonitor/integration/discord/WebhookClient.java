package cn.kurt6.chunkmonitor.integration.discord;

import cn.kurt6.chunkmonitor.integration.discord.message.WebhookMessage;
import org.bukkit.plugin.Plugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Pattern;

public class WebhookClient {
    public static final Pattern WEBHOOK_PATTERN = Pattern.compile("(?:https?://)?(?:\\w+\\.)?discord(?:app)?\\.com/api(?:/v\\d+)?/webhooks/(\\d+)/([\\w-]+)(?:/(?:\\w+)?)?");
    public static final String USER_AGENT = "Webhook(https://github.com/Woolfchik/ChunkMonitor, 1.0.0)";
    public static final long BASE_DELAY_MS = 1000L;
    private final URI webhookUrl;
    private final HttpClient client;
    private final int maxAttempts;
    private final Plugin plugin;

    public WebhookClient(Plugin plugin, int maxAttempts, String webhookUrl) {
        Objects.requireNonNull(webhookUrl, "webhook URL must not be null");
        Objects.requireNonNull(plugin, "plugin must not be null");

        if(!WEBHOOK_PATTERN.matcher(webhookUrl).matches())
            throw new IllegalArgumentException("URL is not valid");

        this.webhookUrl = URI.create(webhookUrl);
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
        this.maxAttempts = maxAttempts;
        this.plugin = plugin;
    }

    public void send(WebhookMessage message) {
        Objects.requireNonNull(message, "message must not be null");
        String messageJson = message.toJson();
        if(messageJson == null) {
            plugin.getLogger().warning("[Discord webhook] Cannot send an empty message");
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            attemptSend(1, messageJson);
        });
    }

    private void attemptSend(int attempt, String message) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(webhookUrl)
                .header("Content-Type", "application/json")
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> processResponseCode(response, attempt, message))
                .exceptionally(exception -> {
                    handleException(attempt, exception, message);
                    return null;
                });
    }

    private void processResponseCode(HttpResponse<String> response, int attempt, String message) {
        int code = response.statusCode();

        if(code == 200 || code == 204) {
            plugin.getLogger().info("[Discord Webhook] Message was sent successfully.");
            return;
        }

        plugin.getLogger().info(String.format("[Discord Webhook] Message was not sent. Code: %d.", code));

        if(attempt > maxAttempts) {
            plugin.getLogger().info("[Discord Webhook] Out of attempts.");
            return;
        }

        long delay = calculateRetryDelay(response, attempt) / 50;
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(
                plugin,
                () -> attemptSend(attempt + 1, message),
                Math.max(delay, 2)
        );
    }

    private long calculateRetryDelay(HttpResponse<String> response, int attempt) {
        int code = response.statusCode();

        if(code == 429) {
            long retryAfter = response.headers().firstValueAsLong("Retry-After").orElse(5);
            return (retryAfter + 1) * 1000L;
        }

        return (long) (BASE_DELAY_MS * (Math.pow(1.9, attempt - 1)));
    }

    private void handleException(int attempt, Throwable exception, String message) {
        plugin.getLogger().warning("[Discord Webhook] Error occurred while trying to send a message: " + exception.getMessage());

        if(attempt > maxAttempts) {
            plugin.getLogger().warning("[Discord Webhook] Canceling message sending. Out of attempts.");
            return;
        }

        long delay = (long) (BASE_DELAY_MS * (Math.pow(1.9, attempt - 1)));
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            attemptSend(attempt + 1, message);
        }, Math.max(delay, 2));
    }
}