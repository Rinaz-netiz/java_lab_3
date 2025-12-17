# 🚖 Autonomous Taxi Management System

Многопоточная система управления парком беспилотных такси.

## Архитектура

```text
+------------------+     +------------------+     +------------------+
|  ClientGenerator |---->|    OrderQueue    |---->|    Dispatcher    |
|     (Thread)     |     |  (BlockingQueue) |     |     (Thread)     |
+------------------+     +------------------+     +--------+---------+
                                                          |
                              assigns orders to           |
                              nearest free taxi           |
                                                          v
+------------------+     +------------------+     +------------------+
|   Statistics     |<----|    TripResult    |<----|    Taxi Pool     |
|    Service       |     |                  |     |   (5 Threads)    |
+------------------+     +------------------+     +------------------+
```

## Механизмы синхронизации

| Компонент | Механизм | Назначение |
|-----------|----------|------------|
| OrderQueue | `LinkedBlockingQueue` | Потокобезопасная очередь заказов |
| Taxi.state | `AtomicReference` | Атомарное изменение состояния |
| Taxi.position | `ReentrantLock` | Защита координат |
| TaxiFleetManager | `CopyOnWriteArrayList` | Безопасный доступ к списку такси |
| StatisticsService | `AtomicInteger/Long` | Атомарные счётчики |

## Требования

- Java 17+

## Запуск

### Linux/macOS

```bash
chmod +x run.sh
./run.sh
```

### Windows

```cmd
run.bat
```

### Makefile

```Bash
make run
```
