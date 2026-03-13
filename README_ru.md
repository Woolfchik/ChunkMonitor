# ChunkMonitor - Плагин Мониторинга Производительности Чанков

**Читать в других языках: [English](README.md)，[中文](README_zh.md)，[Russian](README_ru.md)。**

----------------------------------------------------------------------------------------------------------

Плагин для Minecraft сервера, отслеживающий MSPT чанков, количество выброшенных предметов и сущностей. При превышении заданных пороговых значений отправляет уведомления игрокам, в консоль и в Discord канала через Webhook.

## Ключевые особенности

### Основные функции мониторинга

- **Мониторинг MSPT** — отслеживание времени загрузки отдельных чанков в реальном времени (миллисекунды на тик)
- **Мониторинг количества сущностей** — статистика по общему количеству сущностей в чанке для предотвращения лагов
- **Мониторинг выброшенных предметов** — статистика по количеству предметов в чанке, позволяющая своевременно обнаруживать их скопление

### Система уведомлений

- **Многоканальные уведомления** — независимое управление уведомлениями в игровой чат, консоль и Discord через webhook
- **Механизм задержки (кулдаун)** — не чаще одного уведомления на чанк в течение 5 минут, чтобы избежать спама
- **Точная локализация** — отображается название мира, координаты чанка и диапазон координат блоков
- **Многоязычность** — встроенная поддержка китайского, английского и русского языков, настраиваемые шаблоны сообщений

## Конфигурационный файл

```yaml
# Файл конфигурации плагина Chunk Monitor
# Все временные интервалы указаны в Тиках (1 секунда = 20 тиков)

# Настройка языка
# Поддерживаемые: zh_CN (Упр. Китайский), en_US (Английский), ru_RU (Русский)
language: "en_US"

# Настройка монитора MSPT (Миллисекунд за тик)
mspt:
  enabled: true
  interval: 100  # Интервал обнаружения в тиках
  notification-limit: 50.0  # Лимит миллисекунд

# 实体数量监控配置 / Настройка монитора Числа Сущностей
entity:
  enabled: true
  interval: 80  # Интервал обнаружения в тиках
  notification-limit: 70  # Лимит числа сущностей на чанк

# Настройка монитора Лежащих предметов
item:
  enabled: true
  interval: 60  # Интервал обнаружения в тиках
  notification-limit: 80  # Лимит валяющихся предметов на чанк

# Настройка задержки
cooldown:
  duration: 5  # Задержка оповещений в минутах

# Настройка оповещений
notification:
  broadcast: false  # Отправлять оповещения всем игрокам
  console: true  # Отправлять оповещения в консоль
  discord: true # Отправлять оповещения в discord
  discord-webhook: "" # Ссылка на Discord webhook
  show-coordinates: true  # Показывать диапазон координат

# Настройка сообщений
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
    # Оповещающее сообщение MSPT
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
