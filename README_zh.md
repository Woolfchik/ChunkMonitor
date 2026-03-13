# ChunkMonitor - 区块性能监控插件

**其他语言版本: [English](README.md)，[中文](README_zh.md)，[Russian](README_ru.md)。**

----------------------------------------------------------------------------------------------------------

一个 Minecraft 服务器区块MSPT占用、掉落物、实体监控插件，超过设定的值时，会向玩家、控制台发送通知。

PS：为了搭配MSMP_QQBot开发的

## 功能特性

### 核心监控功能

- **MSPT 监控** - 实时监控单区块加载时间（毫秒每刻）
- **实体数量监控** - 统计单区块内实体总数，防止卡顿
- **掉落物监控** - 统计单区块内掉落物数量，及时发现物品堆积问题

### 通知系统

- **多通道通知** - 支持公屏通知、控制台通知和 Discord 通知独立控制（通过 Webhook）
- **冷却机制** - 同一区块 5 分钟内只通知一次，防止刷屏
- **精确定位** - 显示世界名称、区块坐标和具体方块坐标范围
- **多语言支持** - 内置中文、英文和俄语，可自定义消息模板

## 配置文件


```yaml
# 区块监控插件配置文件
# 所有时间间隔单位为 Ticks (1秒 = 20 Ticks)

# 语言设置
# 支持: zh_CN (简体中文), en_US (English)
language: "en_US"

# MSPT (毫秒每刻) 监控配置
mspt:
  enabled: true
  interval: 100  # 检测间隔，单位 Ticks
  notification-limit: 50.0  # 通知上限，单位毫秒

# 实体数量监控配置
entity:
  enabled: true
  interval: 80  # 检测间隔，单位 Ticks
  notification-limit: 70  # 区块内实体数量上限

# 掉落物数量监控配置
item:
  enabled: true
  interval: 60  # 检测间隔，单位 Ticks
  notification-limit: 80  # 区块内掉落物数量上限

# 冷却配置
cooldown:
  duration: 5  # 通知冷却时间，单位分钟

# 通知配置
notification:
  broadcast: false  # 是否向全服通知
  console: true  # 是否向控制台通知
  discord: true # 发送通知至 discord
  discord-webhook: "" # Discord webhook 网址
  show-coordinates: true  # 是否显示坐标范围

# 消息配置
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
