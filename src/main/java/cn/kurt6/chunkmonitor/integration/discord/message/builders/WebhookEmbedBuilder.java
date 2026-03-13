package cn.kurt6.chunkmonitor.integration.discord.message.builders;


import cn.kurt6.chunkmonitor.integration.discord.message.WebhookEmbed;
import cn.kurt6.chunkmonitor.integration.discord.message.WebhookEmbed.EmbedField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WebhookEmbedBuilder {
    private Integer color;
    private String title;
    private String description;
    private final List<EmbedField> fields = new ArrayList<>();

    public boolean isEmpty() {
        return isEmpty(title)
                && isEmpty(description)
                && isFieldsEmpty();
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isFieldsEmpty() {
        if (fields.isEmpty())
            return true;
        return fields.stream().allMatch(field -> isEmpty(field.name()) && isEmpty(field.value()));
    }

    public void reset() {
        color = null;
        title = null;
        description = null;
        fields.clear();
    }

    public WebhookEmbedBuilder color(Integer color) {
        this.color = color;
        return this;
    }

    public WebhookEmbedBuilder title(String title) {
        this.title = title == null || title.trim().isEmpty() ? null : title;
        return this;
    }

    public WebhookEmbedBuilder description(String description) {
        this.description = description == null || description.trim().isEmpty() ? null : description;
        return this;
    }

    public WebhookEmbedBuilder addFields(EmbedField... fields) {
        Objects.requireNonNull(fields, "fields must not be null");
        if(this.fields.size() + fields.length > 25)
            throw new IllegalStateException("Cannot add more than 25 fields to a embed");

        for(var field : fields) {
            Objects.requireNonNull(field, "field must not be null");
            this.fields.add(field);
        }

        return this;
    }

    public WebhookEmbed build() {
        return new WebhookEmbed(color, title, description, fields);
    }
}
