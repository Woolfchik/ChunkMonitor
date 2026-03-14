package cn.kurt6.chunkmonitor;

import cn.kurt6.chunkmonitor.integration.discord.WebhookClient;
import cn.kurt6.chunkmonitor.integration.discord.message.WebhookEmbed.EmbedField;
import cn.kurt6.chunkmonitor.integration.discord.message.builders.WebhookEmbedBuilder;
import cn.kurt6.chunkmonitor.integration.discord.message.builders.WebhookMessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChunkMonitorPlugin extends JavaPlugin {

    private Map<String, Long> chunkCooldown = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;
    private boolean isFolia = false;
    private String language = "en_US";
    private Map<String, Map<String, String>> messages = new ConcurrentHashMap<>();
    private WebhookClient webhookClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        detectServerType();
        loadLanguage();

        scheduler = Executors.newScheduledThreadPool(3);

        if(getConfig().getBoolean("notification.discord")) {
            String webhookUrl = getConfig().getString("discord.webhook");
            int maxWebhookAttempts = getConfig().getInt("discord.max-attempts");
            webhookClient = new WebhookClient(this, maxWebhookAttempts, webhookUrl);
        }

        String msg = getMessage("enabled_message");
        getLogger().info(msg);

        String typeMsg = getMessage("server_type_message")
                .replace("%type%", isFolia ? "Folia" : "Paper/Spigot");
        getLogger().info(typeMsg);

        startTasks();
    }

    @Override
    public void onDisable() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
        getLogger().info(getMessage("disabled_message"));
    }

    private void detectServerType() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
    }

    private void loadLanguage() {
        language = getConfig().getString("language", "en_US");
        ConfigurationSection messageSection = getConfig().getConfigurationSection("messages." + language);

        if (messageSection != null) {
            Map<String, String> langMessages = new ConcurrentHashMap<>();
            for (String key : messageSection.getKeys(true)) {
                langMessages.put(key, messageSection.getString(key, ""));
            }
            messages.put(language, langMessages);
        }
    }

    private String getMessage(String key) {
        Map<String, String> langMessages = messages.get(language);
        if (langMessages == null) {
            return "[" + key + "]";
        }
        return langMessages.getOrDefault(key, "[" + key + "]");
    }

    private void startTasks() {
        if (getConfig().getBoolean("mspt.enabled")) {
            long interval = getConfig().getLong("mspt.interval") * 50;
            scheduler.scheduleAtFixedRate(this::checkMspt, interval, interval, TimeUnit.MILLISECONDS);
        }

        if (getConfig().getBoolean("entity.enabled")) {
            long interval = getConfig().getLong("entity.interval") * 50;
            scheduler.scheduleAtFixedRate(this::checkEntity, interval, interval, TimeUnit.MILLISECONDS);
        }

        if (getConfig().getBoolean("item.enabled")) {
            long interval = getConfig().getLong("item.interval") * 50;
            scheduler.scheduleAtFixedRate(this::checkItem, interval, interval, TimeUnit.MILLISECONDS);
        }
    }

    private void checkMspt() {
        try {
            double limit = getConfig().getDouble("mspt.notification-limit");

            for (World world : Bukkit.getWorlds()) {
                Chunk[] chunks = world.getLoadedChunks();
                for (Chunk chunk : chunks) {
                    String chunkKey = getChunkKey(chunk);
                    if (isInCooldown(chunkKey)) continue;

                    try {
                        double mspt = calculateChunkLoad(chunk);
                        if (mspt > limit) {
                            getServer().getScheduler().runTaskAsynchronously(this,
                                    () -> notifyChunk(chunk, "mspt_alert", String.format("%.2f", mspt), limit)
                            );
                            setCooldown(chunkKey);
                        }
                    } catch (Exception e) {
                        // Silent processing
                    }
                }
            }
        } catch (Exception e) {
            getLogger().fine("MSPT detection anomaly");
        }
    }

    private void checkEntity() {
        try {
            int limit = getConfig().getInt("entity.notification-limit");

            for (World world : Bukkit.getWorlds()) {
                Chunk[] chunks = world.getLoadedChunks();
                for (Chunk chunk : chunks) {
                    String chunkKey = getChunkKey(chunk);
                    if (isInCooldown(chunkKey)) continue;

                    int entityCount = chunk.getEntities().length;
                    if (entityCount > limit) {
                        getServer().getScheduler().runTaskAsynchronously(this,
                                () -> notifyChunk(chunk, "entity_alert", entityCount, limit)
                        );
                        setCooldown(chunkKey);
                    }
                }
            }
        } catch (Exception e) {
            getLogger().fine("Entity detection anomaly");
        }
    }

    private void checkItem() {
        try {
            int limit = getConfig().getInt("item.notification-limit");

            for (World world : Bukkit.getWorlds()) {
                Chunk[] chunks = world.getLoadedChunks();
                for (Chunk chunk : chunks) {
                    String chunkKey = getChunkKey(chunk);
                    if (isInCooldown(chunkKey)) continue;

                    long itemCount = Arrays.stream(chunk.getEntities())
                            .filter(entity -> entity instanceof Item)
                            .count();

                    if (itemCount > limit) {
                        getServer().getScheduler().runTaskAsynchronously(this,
                                () -> notifyChunk(chunk, "item_alert", itemCount, limit)
                        );
                        setCooldown(chunkKey);
                    }
                }
            }
        } catch (Exception e) {
            getLogger().fine("Dropped item detection anomaly");
        }
    }

    private double calculateChunkLoad(Chunk chunk) {
        Entity[] entities = chunk.getEntities();
        double load = entities.length * 0.5;

        for (Entity entity : entities) {
            if (entity instanceof Item) {
                load += 0.1;
            } else {
                load += 0.3;
            }
        }

        return Math.min(load, 100.0);
    }

    private String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }

    private boolean isInCooldown(String chunkKey) {
        Long cooldownTime = chunkCooldown.get(chunkKey);
        if (cooldownTime == null) return false;

        if (System.currentTimeMillis() > cooldownTime) {
            chunkCooldown.remove(chunkKey);
            return false;
        }
        return true;
    }

    private void setCooldown(String chunkKey) {
        long cooldownMs = getConfig().getLong("cooldown.duration") * 60 * 1000;
        chunkCooldown.put(chunkKey, System.currentTimeMillis() + cooldownMs);
    }

    private void notifyChunk(Chunk chunk, String messageKey, Object value, Object limit) {
        if(!shouldSend()) {
            return;
        }

        String world = chunk.getWorld().getName();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Calculating coordinate range (each chunk 16×16 blocks)
        int coordMinX = chunkX * 16;
        int coordMaxX = coordMinX + 15;
        int coordMinZ = chunkZ * 16;
        int coordMaxZ = coordMinZ + 15;

        var chunkCoords = "[" + chunkX + ", " + chunkZ + "]";
        var coords = "X [" + coordMinX + " - " + coordMaxX + "], Z [" + coordMinZ + " - " + coordMaxZ + "]";

        var worldTranslation = getMessage("world");
        var chunkTranslation = getMessage("chunk");
        var coordsTranslation = getMessage("coordinates");
        var valueTranslation = getMessage("value");
        var limitTranslation = getMessage("limit");

        String message = "";
        if(getConfig().getBoolean("notification.broadcast") || getConfig().getBoolean("notification.console")) {
            message = "[" + getMessage("sender") + "] " +
                    worldTranslation +
                    ": " +
                    world +
                    " | " +
                    chunkTranslation +
                    ": " + chunkCoords + " | " +
                    coordsTranslation +
                    ": " + coords + " | " +
                    getMessage(messageKey) +
                    ": " + value + " (" + limitTranslation + ": " + limit + ")";
        }

        if (getConfig().getBoolean("notification.broadcast")) {
            try {
                // Send only to online players, do not output to console
                for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
                    if(player.hasPermission("chunkmonitor.admin")) {
                        player.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                getLogger().fine("Broadcast failed");
            }
        }

        if (getConfig().getBoolean("notification.console")) {
            getLogger().warning(message);
        }

        if (getConfig().getBoolean("notification.discord")) {
            webhookClient.send(
                    new WebhookMessageBuilder()
                            .username(getConfig().getString("discord.bot.username"))
                            .avatarUrl(getConfig().getString("discord.bot.avatar-url"))
                            .addEmbeds(
                                    new WebhookEmbedBuilder()
                                            .color(getEmbedColor())
                                            .title(getConfig().getString("discord.embed.title"))
                                            .description(getMessage("notification.type") + ": " + getMessage("notification." + messageKey))
                                            .addFields(
                                                    new EmbedField(worldTranslation, world),
                                                    new EmbedField(chunkTranslation, chunkCoords),
                                                    new EmbedField(coordsTranslation, coords),
                                                    new EmbedField(valueTranslation, String.valueOf(value)),
                                                    new EmbedField(limitTranslation, String.valueOf(limit))
                                            )
                                            .build()
                            )
                            .build()
            );
        }
    }

    private int getEmbedColor() {
        String color = getConfig().getString("discord.embed.color");
        if(color == null || color.isBlank())
            return 0xF7E525;

        return Integer.decode(color);
    }

    private boolean shouldSend() {
        return getConfig().getBoolean("notification.broadcast")
                || getConfig().getBoolean("notification.console")
                || getConfig().getBoolean("notification.discord");
    }

    @Override
    public void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        if (!getDataFolder().toPath().resolve("config.yml").toFile().exists()) {
            saveResource("config.yml", false);
        }
    }
}