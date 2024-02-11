package fuzs.puzzleslib.api.core.v1;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Access to mod data.
 */
public interface ModContainer {

    /**
     * @return the mod id
     */
    String getModId();

    /**
     * @return the mod name, possibly the mod id if not present
     */
    String getDisplayName();

    /**
     * @return a description for the mod
     */
    String getDescription();

    /**
     * @return the current mod version
     */
    String getVersion();

    /**
     * @return licenses for the mod
     */
    Collection<String> getLicenses();

    /**
     * @return a list of mod authors
     */
    Collection<String> getAuthors();

    /**
     * @return a list of credits like contributors
     */
    Collection<String> getCredits();

    /**
     * @return various contact information like social profiles and urls for mod downloads and issue reports
     */
    Map<String, String> getContactTypes();

    /**
     * Finds a resource in the mod jar file.
     *
     * @param path resource name, if entered as single string path components are separated using "/"
     * @return path to the resource if it exists, otherwise empty
     */
    Optional<Path> findResource(String... path);

    /**
     * @return other mods provided via jar-in-jar systems
     */
    Collection<ModContainer> getChildren();

    /**
     * @return all mods including self and mods provided via jar-in-jar systems
     */
    default Stream<ModContainer> getAllChildren() {
        return Stream.concat(Stream.of(this), this.getChildren().stream().flatMap(ModContainer::getAllChildren));
    }

    /**
     * @return parent mod when provided via jar-in-jar systems
     */
    @Nullable
    ModContainer getParent();

    @ApiStatus.Internal
    static Map<String, ModContainer> toModList(Stream<? extends ModContainer> stream) {
        return stream.sorted(Comparator.comparing(ModContainer::getModId))
                .collect(ImmutableMap.<ModContainer, String, ModContainer>toImmutableMap(ModContainer::getModId, Function.identity()));
    }
}
