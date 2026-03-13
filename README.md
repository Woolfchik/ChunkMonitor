# ChunkMonitor - Block Performance Monitoring Plugin

**Read this in other languages: [English](README.md)，[中文](README_zh.md)，[Russian](README_ru.md)。**

----------------------------------------------------------------------------------------------------------

A Minecraft server block MSPT usage, dropped items, and entity monitoring plugin. When values exceed the configured threshold, notifications are sent to players and console.

## Feature Highlights

### Core Monitoring Functions

- **MSPT Monitoring** - Real-time monitoring of individual chunk loading time (milliseconds per tick)
- **Entity Count Monitoring** - Statistics for total entities within a chunk, preventing lag
- **Dropped Items Monitoring** - Statistics for dropped items within a chunk, promptly detecting item accumulation issues

### Notification System

- **Multi-Channel Notifications** - Support for independent control of in-game chat notifications, console notifications and Discord notifications via webhook
- **Cooldown Mechanism** - Only notify once per chunk within 5 minutes, preventing spam
- **Precise Localization** - Displays world name, chunk coordinates, and specific block coordinate ranges
- **Multi-Language Support** - Built-in Chinese, English and Russian, customizable message templates

## Configuration File

```yaml
# Chunk Monitor Plugin Configuration File
# All time intervals are in Ticks (1 second = 20 Ticks)

# Language Setting
# Supported: zh_CN (Simplified Chinese), en_US (English)
language: "en_US"

# MSPT Monitoring Configuration
mspt:
  enabled: true
  interval: 100  # Detection interval in Ticks
  notification-limit: 50.0  # Notification limit in milliseconds

# Entity Count Monitoring Configuration
entity:
  enabled: true
  interval: 80  # Detection interval in Ticks
  notification-limit: 70  # Entity count limit per chunk

# Item Drop Monitoring Configuration
item:
  enabled: true
  interval: 60  # Detection interval in Ticks
  notification-limit: 80  # Item drop count limit per chunk

# Cooldown Configuration
cooldown:
  duration: 5  # Notification cooldown in minutes

# Notification Configuration
notification:
  broadcast: false  # Send notification to all players
  console: true  # Send notification to console
  discord: true # Send notification to discord
  discord-webhook: "" # Discord webhook url
  show-coordinates: true  # Show coordinate range

# Message Configuration
messages:
  zh_CN:
    # MSPT 通知消息
    mspt_alert: "[区块监控] 世界: %world% | 区块: [%chunk_x%, %chunk_z%] | 坐标范围: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] | MSPT: %value% ms (上限: %limit% ms)"

    # 实体数量通知消息
    entity_alert: "[区块监控] 世界: %world% | 区块: [%chunk_x%, %chunk_z%] | 坐标范围: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] | 实体数量: %value% (上限: %limit%)"

    # 掉落物通知消息
    item_alert: "[区块监控] 世界: %world% | 区块: [%chunk_x%, %chunk_z%] | 坐标范围: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] | 掉落物: %value% (上限: %limit%)"

    # 启用消息
    enabled_message: "[区块监控] 插件已加载"
    server_type_message: "服务器类型: %type%"
    disabled_message: "[区块监控] 插件已卸载"

  en_US:
    # MSPT Alert Message
    mspt_alert: "[ChunkMonitor] World: %world% | Chunk: [%chunk_x%, %chunk_z%] | Coordinates: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] | MSPT: %value% ms (Limit: %limit% ms)"

    # Entity Count Alert Message
    entity_alert: "[ChunkMonitor] World: %world% | Chunk: [%chunk_x%, %chunk_z%] | Coordinates: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] | Entity Count: %value% (Limit: %limit%)"

    # Item Drop Alert Message
    item_alert: "[ChunkMonitor] World: %world% | Chunk: [%chunk_x%, %chunk_z%] | Coordinates: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] | Item Drops: %value% (Limit: %limit%)"

    # Enable Message
    enabled_message: "[ChunkMonitor] Plugin enabled"
    server_type_message: "Server Type: %type%"
    disabled_message: "[ChunkMonitor] Plugin disabled"

  ru_RU:
    # Оповещающее сообщение МСЗТ
    mspt_alert: "[ChunkMonitor] Мир: %world% | Чанк: [%chunk_x%, %chunk_z%] | Координаты: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] | МСЗТ: %value% мс (Лимит: %limit% мс)"

    # Оповещающее сообщение Количество Сущностей
    entity_alert: "[ChunkMonitor] Мир: %world% | Чанк: [%chunk_x%, %chunk_z%] | Координаты: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] | Число сущностей: %value% (Лимит: %limit%)"

    # Оповещающее сообщение Выкинутые Предметы
    item_alert: "[ChunkMonitor] Мир: %world% | Чанк: [%chunk_x%, %chunk_z%] | Координаты: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] | Выкинутых предметов: %value% (Лимит: %limit%)"

    # Сообщения включения/выключения
    enabled_message: "[ChunkMonitor] Плагин включен"
    server_type_message: "Тип сервера: %type%"
    disabled_message: "[ChunkMonitor] Плагин выключен"
```
