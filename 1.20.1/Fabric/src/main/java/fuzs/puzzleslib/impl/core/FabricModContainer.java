package fuzs.puzzleslib.impl.core;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

public final class FabricModContainer implements ModContainer {
    private final net.fabricmc.loader.api.ModContainer container;
    private final ModMetadata metadata;
    private final List<ModContainer> children;
    @Nullable
    private ModContainer parent;

    public FabricModContainer(net.fabricmc.loader.api.ModContainer container) {
        this.container = container;
        this.metadata = container.getMetadata();
        this.children = Lists.newArrayList();
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
    public Optional<Path> findResource(String... path) {
        return this.container.findPath(String.join("/", path));
    }

    @Override
    public Collection<ModContainer> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    @Nullable
    @Override
    public ModContainer getParent() {
        return this.parent;
    }

    public void setParent(@Nullable FabricModContainer parent) {
        if (parent != null && parent != this) {
            this.parent = parent;
            parent.addChild(this);
        }
    }

    private void addChild(ModContainer modContainer) {
        Objects.requireNonNull(modContainer, "child is null");
        if (!this.children.contains(modContainer)) {
            this.children.add(modContainer);
            this.children.sort(Comparator.comparing(ModContainer::getModId));
        }
    }

    public net.fabricmc.loader.api.ModContainer getFabricModContainer() {
        return this.container;
    }
}
