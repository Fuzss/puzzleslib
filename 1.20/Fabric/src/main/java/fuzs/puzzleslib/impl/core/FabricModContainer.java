package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record FabricModContainer(net.fabricmc.loader.api.ModContainer container, ModMetadata metadata) implements ModContainer {

    public FabricModContainer(net.fabricmc.loader.api.ModContainer container) {
        this(container, container.getMetadata());
    }

    @Override
    public String getModId() {
        return this.metadata.getId();
    }

    @Override
    public String getDisplayName() {
        return this.metadata.getName();
    }

    @Override
    public String getDescription() {
        return this.metadata.getDescription();
    }

    @Override
    public String getVersion() {
        return this.metadata.getVersion().getFriendlyString();
    }

    @Override
    public Collection<String> getLicenses() {
        return this.metadata.getLicense();
    }

    @Override
    public Collection<String> getAuthors() {
        return this.metadata.getAuthors().stream().map(Person::getName).toList();
    }

    @Override
    public Collection<String> getCredits() {
        return this.metadata.getContributors().stream().map(Person::getName).toList();
    }

    @Override
    public Map<String, String> getContactTypes() {
        return this.metadata.getContact().asMap();
    }

    @Override
    public Optional<String> getIconPath(int size) {
        return this.metadata.getIconPath(size);
    }

    @Override
    public boolean isClientOnly() {
        return this.metadata.getEnvironment() == ModEnvironment.CLIENT;
    }

    @Override
    public boolean isLibrary() {
        // Fabric does not provide a way to mark mods as libraries, this is as close as it gets
        return this.metadata.containsCustomValue("fabric-loom:generated") && this.metadata.getCustomValue("fabric-loom:generated").getAsBoolean();
    }

    @Override
    public Optional<Path> findResource(String... path) {
        return this.container.findPath(String.join("/", path));
    }

    @Override
    public List<String> getDependencyIds() {
        return this.metadata.getDependencies().stream().filter(dependency -> dependency.getKind().isPositive() && !dependency.getKind().isSoft()).map(ModDependency::getModId).toList();
    }

    @Override
    public Optional<String> getUpdateUrl() {
        // Fabric has no built-in update checker, third-party mods such as Mod Menu can provide such functionality
        return Optional.empty();
    }

    @Override
    public boolean isUpdateAvailable() {
        // Fabric has no built-in update checker, third-party mods such as Mod Menu can provide such functionality
        return false;
    }
}
