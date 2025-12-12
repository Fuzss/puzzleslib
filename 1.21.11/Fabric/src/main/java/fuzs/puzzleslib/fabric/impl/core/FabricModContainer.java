package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FabricModContainer implements ModContainer {
    private final net.fabricmc.loader.api.ModContainer container;
    private final ModMetadata metadata;
    private final List<ModContainer> children;
    @Nullable
    private ModContainer parent;

    public FabricModContainer(net.fabricmc.loader.api.ModContainer container) {
        this.container = container;
        this.metadata = container.getMetadata();
        this.children = new ArrayList<>();
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

    public net.fabricmc.loader.api.ModContainer getFabricModContainer() {
        return this.container;
    }

    private void setParent(@Nullable FabricModContainer parent) {
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

    public static Stream<? extends ModContainer> getFabricModContainers() {
        Map<net.fabricmc.loader.api.ModContainer, FabricModContainer> allMods = FabricLoader.getInstance()
                .getAllMods()
                .stream()
                .map(FabricModContainer::new)
                .collect(Collectors.toMap(FabricModContainer::getFabricModContainer,
                        Function.identity(),
                        (FabricModContainer o1, FabricModContainer o2) -> {
                            o2.setParent(o1);
                            return o1;
                        }));
        for (FabricModContainer modContainer : allMods.values()) {
            modContainer.getFabricModContainer()
                    .getContainedMods()
                    .stream()
                    .map(allMods::get)
                    .forEach((FabricModContainer childModContainer) -> {
                        childModContainer.setParent(modContainer);
                    });
        }

        return allMods.values().stream();
    }
}
