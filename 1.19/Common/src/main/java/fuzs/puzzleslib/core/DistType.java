package fuzs.puzzleslib.core;

/**
 * a type of Minecraft environment, for distinguishing between physical client and server
 */
public enum DistType {
    /**
     * the physical client, capable of playing singleplayer and multiplayer sessions
     * has full access to all game resources
     */
    CLIENT,
    /**
     * the physical server, used for hosting multiplayer sessions
     * does not have access to client resources
     */
    SERVER
}
