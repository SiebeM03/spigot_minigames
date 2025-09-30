# Logging System

This plugin includes a simple logging system that provides consistent logging throughout the codebase.

## Features

- **Multiple log levels**: Debug, Info, Warning, Error
- **Class identification**: Every log message includes the class name for easy identification
- **Aligned formatting**: All log messages are formatted with consistent spacing for easy reading
- **Lazy evaluation**: Debug messages are only evaluated when debug logging is enabled
- **Bukkit integration**: Uses the standard Bukkit/Spigot logging system
- **Easy to use**: Simple extension functions for getting loggers

## Configuration

Enable debug logging by setting `debug-logging: true` in your `config.yml`:

```yaml
debug-logging: true
```

## Usage

### Getting a Logger

```kotlin
class MyClass {
    private val logger = getLogger() // Extension function for any class
    
    fun doSomething() {
        logger.info("Something happened!")
    }
}
```

Or for specific categories:

```kotlin
val logger = LoggerManager.getLogger("MyCategory")
```

### Log Levels

```kotlin
// Debug messages (only shown when debug-logging is enabled)
logger.debug("Detailed debug information")
logger.debug { "Lazy evaluation: ${expensiveOperation()}" }

// Info messages (always shown)
logger.info("General information")
logger.info { "Lazy evaluation: ${expensiveOperation()}" }

// Warning messages (always shown)
logger.warning("Something might be wrong")
logger.warning { "Lazy evaluation: ${expensiveOperation()}" }

// Error messages (always shown)
logger.error("Something went wrong")
logger.error("Something went wrong", exception)
logger.error { "Lazy evaluation: ${expensiveOperation()}" }
```

**Output Example:**
```
[GameManager        ]  [DEBUG]  Creating new game for mode: SKYWARS
[GameManager        ]  [INFO]   Created new SKYWARS game with UUID: 12345678-1234-1234-1234-123456789abc
[Game              ]  [INFO]   Game initialized: SKYWARS game in world 'SKYWARS-template1-12345678-1234-1234-1234-123456789abc'
[LoggerManager     ]  [INFO]   LoggerManager initialized with debug: true
[PlayerManager     ]  [WARN]   Player limit reached for game 12345678-1234-1234-1234-123456789abc
[BlockManager      ]  [ERROR]  Failed to load block configuration
```

### Lazy Evaluation

Use lambda functions for expensive operations that should only be executed when the log level is enabled:

```kotlin
logger.debug { "Player data: ${getExpensivePlayerData()}" }
```

This way, `getExpensivePlayerData()` is only called when debug logging is enabled.

## Examples

### In Game Classes

```kotlin
class MyGame : Game(WorldType.SKYWARS) {
    private val logger = getLogger()
    
    override fun startGame() {
        logger.info { "Starting ${worldType.name} game" }
        logger.debug { "Game world: ${world.name}" }
        // ... game logic
    }
}
```

### In Event Listeners

```kotlin
class MyListener : Listener {
    private val logger = getLogger()
    
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        logger.info { "Player ${event.player.name} joined the server" }
        logger.debug { "Player location: ${event.player.location}" }
    }
}
```

### Error Handling

```kotlin
try {
    riskyOperation()
} catch (e: Exception) {
    logger.error("Failed to perform risky operation", e)
}
```

## Best Practices

1. **Use lazy evaluation** for debug messages with expensive operations
2. **Include context** in log messages (player names, game IDs, etc.)
3. **Use appropriate log levels**:
   - `debug`: Detailed information for debugging
   - `info`: General information about what's happening
   - `warning`: Something unexpected but not critical
   - `error`: Something went wrong
4. **Keep messages concise** but informative
5. **Use structured information** when possible (include IDs, names, etc.)
