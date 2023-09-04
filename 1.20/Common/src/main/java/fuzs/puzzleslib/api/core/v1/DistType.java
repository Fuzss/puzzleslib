package fuzs.puzzleslib.api.core.v1;

/**
 * a type of Minecraft environment, for distinguishing between physical client and server
 */
@Deprecated(forRemoval = true)
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
