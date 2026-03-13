package cn.kurt6.chunkmonitor.integration.discord.message.builders;


import cn.kurt6.chunkmonitor.integration.discord.message.WebhookEmbed;
import cn.kurt6.chunkmonitor.integration.discord.message.WebhookMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WebhookMessageBuilder {
    private String username;
    private String avatarUrl;
    private StringBuilder content = new StringBuilder();
    private List<WebhookEmbed> embeds = new ArrayList<>();
    boolean suppressNotifications = false;
    private String threadName;

    public boolean isEmpty() {
        return content.isEmpty() && embeds.isEmpty();
    }

    public void reset() {
        resetContent();
        resetEmbeds();
        username = null;
        avatarUrl = null;
        suppressNotifications = false;
        threadName = null;
    }

    public void resetContent() {
        content.setLength(0);
    }

    public void resetEmbeds() {
        embeds.clear();
    }

    public WebhookMessageBuilder username(String username) {
        this.username = username == null || username.trim().isEmpty() ? null : username;
        return this;
    }

    public WebhookMessageBuilder avatarUrl(String url) {
        avatarUrl = url == null || url.trim().isEmpty() ? null : url;
        return this;
    }

    public WebhookMessageBuilder setContent(String content) {
        if(content != null && content.length() > 2000)
            throw new IllegalArgumentException("Content may not exceed 2000 characters!");

        this.content.setLength(0);
        if (content != null && !content.isEmpty())
            this.content.append(content);

        return this;
    }

    public WebhookMessageBuilder content(String content) {
        Objects.requireNonNull(content, "content must not be null");
        if(this.content.length() + content.length() > 2000)
            throw new IllegalArgumentException("Content may not exceed 2000 characters!");
        this.content.append(content);

        return this;
    }

    public WebhookMessageBuilder suppressNotifications() {
        suppressNotifications = true;
        return this;
    }

    public WebhookMessageBuilder suppressNotifications(boolean suppress) {
        suppressNotifications = suppress;
        return this;
    }

    public WebhookMessageBuilder thread(String threadName) {
        this.threadName = threadName;
        return this;
    }

    public WebhookMessageBuilder addEmbeds(WebhookEmbed... embeds) {
        Objects.requireNonNull(embeds, "embeds must not be null");
        if(this.embeds.size() + embeds.length > 10)
            throw new IllegalStateException("Cannot add more than 10 embeds to a message");

        for(var embed : embeds) {
            Objects.requireNonNull(embed, "embed must not be null");
            this.embeds.add(embed);
        }

        return this;
    }

    public WebhookMessage build() {
        if(isEmpty())
            throw new IllegalStateException("Cannot build an empty message!");

        return new WebhookMessage(username, avatarUrl, content.toString(), embeds, suppressNotifications, threadName);
    }
}