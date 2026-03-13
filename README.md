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

- **Dual-Channel Notifications** - Support for independent control of in-game chat notifications, console notifications and Discord notification thru webhook.
- **Cooldown Mechanism** - Only notify once per chunk within 5 minutes, preventing spam
- **Precise Localization** - Displays world name, chunk coordinates, and specific block coordinate ranges
- **Multi-Language Support** - Built-in Chinese, English and Russian, customizable message templates

## Configuration File

```yaml
# 区块监控插件配置文件
# Chunk Monitor Plugin Configuration File
# Файл конфигурации плагина Chunk Monitor
# 所有时间间隔单位为 Ticks (1秒 = 20 Ticks)
# All time intervals are in Ticks (1 second = 20 Ticks)
# Все временные интервалы указаны в Тиках (1 секунда = 20 тиков)

# 语言设置 / Language Setting / Настройка языка
# 支持: zh_CN (简体中文), en_US (English)
# Supported: zh_CN (Simplified Chinese), en_US (English)
# Поддерживаемые: zh_CN (Упр. Китайский), en_US (Английский), ru_RU (Русский)
language: "en_US"

# MSPT (毫秒每刻) 监控配置 / MSPT Monitoring Configuration / Настройка монитора МСЗТ (Миллисекунд за тик)
mspt:
  enabled: true
  interval: 100  # 检测间隔，单位 Ticks / Detection interval in Ticks / Интервал обнаружения в тиках
  notification-limit: 50.0  # 通知上限，单位毫秒 / Notification limit in milliseconds / Лимит миллисекунд

# 实体数量监控配置 / Entity Count Monitoring Configuration / Настройка монитора Числа Сущностей
entity:
  enabled: true
  interval: 80  # 检测间隔，单位 Ticks / Detection interval in Ticks / Интервал обнаружения в тиках
  notification-limit: 70  # 区块内实体数量上限 / Entity count limit per chunk / Лимит числа сущностей на чанк

# 掉落物数量监控配置 / Item Drop Monitoring Configuration / Настройка монитора Лежащих предметов
item:
  enabled: true
  interval: 60  # 检测间隔，单位 Ticks / Detection interval in Ticks / Интервал обнаружения в тиках
  notification-limit: 80  # 区块内掉落物数量上限 / Item drop count limit per chunk / Лимит валяющихся предметов на чанк

# 冷却配置 / Cooldown Configuration / Настройка задержки ("Перезарядки")
cooldown:
  duration: 5  # 通知冷却时间，单位分钟 / Notification cooldown in minutes / Задержка оповещений в минутах

# 通知配置 / Notification Configuration / Настройка оповещений
notification:
  broadcast: false  # 是否向全服通知 / Send notification to all players / Отправлять оповещения всем игрокам
  console: true  # 是否向控制台通知 / Send notification to console / Отправлять оповещения в консоль
  discord: true # 发送通知至 discord / Send notification to discord / Отправлять оповещения в discord
  discord-webhook: "" # Discord webhook 网址 / Discord webhook url / Ссылка на Discord webhook
  show-coordinates: true  # 是否显示坐标范围 / Show coordinate range / Показывать "разброс координат"

# 消息配置 / Message Configuration / Настройка сообщений
messages:
  zh_CN:
    # MSPT 通知消息
    mspt_alert: "§c[区块监控] §e世界: %world% §r| §e区块: [%chunk_x%, %chunk_z%] §r| §e坐标范围: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cMSPT: %value% ms §r(上限: %limit% ms)"

    # 实体数量通知消息
    entity_alert: "§c[区块监控] §e世界: %world% §r| §e区块: [%chunk_x%, %chunk_z%] §r| §e坐标范围: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §c实体数量: %value% §r(上限: %limit%)"

    # 掉落物通知消息
    item_alert: "§c[区块监控] §e世界: %world% §r| §e区块: [%chunk_x%, %chunk_z%] §r| §e坐标范围: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §c掉落物: %value% §r(上限: %limit%)"

    # 启用消息
    enabled_message: "§a[区块监控] 插件已加载"
    server_type_message: "§6服务器类型: %type%"
    disabled_message: "§c[区块监控] 插件已卸载"

  en_US:
    # MSPT Alert Message
    mspt_alert: "§c[ChunkMonitor] §eWorld: %world% §r| §eChunk: [%chunk_x%, %chunk_z%] §r| §eCoordinates: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cMSPT: %value% ms §r(Limit: %limit% ms)"

    # Entity Count Alert Message
    entity_alert: "§c[ChunkMonitor] §eWorld: %world% §r| §eChunk: [%chunk_x%, %chunk_z%] §r| §eCoordinates: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cEntity Count: %value% §r(Limit: %limit%)"

    # Item Drop Alert Message
    item_alert: "§c[ChunkMonitor] §eWorld: %world% §r| §eChunk: [%chunk_x%, %chunk_z%] §r| §eCoordinates: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cItem Drops: %value% §r(Limit: %limit%)"

    # Enable Message
    enabled_message: "§a[ChunkMonitor] Plugin enabled"
    server_type_message: "§6Server Type: %type%"
    disabled_message: "§c[ChunkMonitor] Plugin disabled"

  ru_RU:
    # Оповещающее сообщение МСЗТ
    mspt_alert: "§c[ChunkMonitor] §eМир: %world% §r| §eЧанк: [%chunk_x%, %chunk_z%] §r| §eКоординаты: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cМСЗТ: %value% мс §r(Лимит: %limit% мс)"

    # Оповещающее сообщение Количество Сущностей
    entity_alert: "§c[ChunkMonitor] §eМир: %world% §r| §eЧанк: [%chunk_x%, %chunk_z%] §r| §eКоординаты: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cЧисло сущностей: %value% §r(Лимит: %limit%)"

    # Оповещающее сообщение Выкинутые Предметы
    item_alert: "§c[ChunkMonitor] §eМир: %world% §r| §eЧанк: [%chunk_x%, %chunk_z%] §r| §eКоординаты: X [%coord_min_x% ~ %coord_max_x%] Z [%coord_min_z% ~ %coord_max_z%] §r| §cВыкинутых предметов: %value% §r(Лимит: %limit%)"

    # Сообщения включения/выключения
    enabled_message: "[ChunkMonitor] Плагин включен"
    server_type_message: "Тип сервера: %type%"
    disabled_message: "[ChunkMonitor] Плагин выключен"
```
