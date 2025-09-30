package minigames.core.game

enum class GameState {
    /** Waiting for players to join, game is not started yet */
    WAITING_FOR_PLAYERS,
    /** Game is starting, countdown is running */
    STARTING,
    /** Game is running, players are playing */
    PLAYING,
    /** Game is ended, players are teleported to the lobby */
    ENDED
}