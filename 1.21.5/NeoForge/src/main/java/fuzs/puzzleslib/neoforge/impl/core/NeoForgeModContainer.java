package fuzs.puzzleslib.neoforge.impl.core;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.i18n.MavenVersionTranslator;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class NeoForgeModContainer implements ModContainer {
    private final IModInfo metadata;
    private final List<ModContainer> children;
    @Nullable
    private ModContainer parent;

    public NeoForgeModContainer(IModInfo metadata) {
        this.metadata = metadata;
        this.children = new ArrayList<>();
    }

    @Override
    public String getModId() {
        return this.metadata.getModId();
    }

    @Override
    public String getDisplayName() {
        return this.metadata.getDisplayName();
    }

    @Override
    public String getDescription() {
        return this.metadata.getDescription();
    }

    @Override
    public String getVersion() {
        return MavenVersionTranslator.artifactVersionToString(this.metadata.getVersion());
    }

    @Override
    public Collection<String> getLicenses() {
        return List.of(this.metadata.getOwningFile().getLicense());
    }

    @Override
    public Collection<String> getAuthors() {
        return this.getConfigElement("authors");
    }

    @Override
    public Collection<String> getCredits() {
        return this.getConfigElement("credits");
    }

    private List<String> getConfigElement(String configKey) {
        return this.metadata.getConfig().getConfigElement(configKey).map(authors -> {
            if (authors instanceof Collection<?> collection) {
                return collection.stream().map(Object::toString).toList();
            } else {
                return List.of(authors.toString());
            }
        }).orElseGet(List::of);
    }

    @Override
    public Map<String, String> getContactTypes() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        this.metadata.getConfig()
                .<String>getConfigElement("displayURL")
                .or(() -> this.metadata.getModURL().map(URL::toString))
                .ifPresent(s -> builder.put("homepage", s));
        if (this.metadata.getOwningFile() instanceof ModFileInfo modFileInfo) {
            Optional.ofNullable(modFileInfo.getIssueURL()).map(URL::toString).ifPresent(s -> builder.put("issues", s));
        }
        return builder.build();
    }

    @Override
    public Optional<Path> findResource(String... path) {
        return Optional.of(this.metadata.getOwningFile().getFile().findResource(path)).filter(Files::exists);
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

    public void setParent(@Nullable NeoForgeModContainer parent) {
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

    public URI getURI() {
        return this.metadata.getOwningFile().getFile().getSecureJar().moduleDataProvider().uri();
    }

    public static Stream<? extends ModContainer> getNeoForgeModContainers() {
        Map<String, NeoForgeModContainer> allMods = getNeoForgeModList().stream()
                .map(NeoForgeModContainer::new)
                .collect(Collectors.toMap(modContainer -> {
                    // alternatively use raw variant for escaped octets
                    return modContainer.getURI().getSchemeSpecificPart();
                }, Function.identity(), (NeoForgeModContainer o1, NeoForgeModContainer o2) -> {
                    o2.setParent(o1);
                    return o1;
                }));
        for (NeoForgeModContainer modContainer : allMods.values()) {
            if (modContainer.getURI().getScheme().equals("union")) {
                // alternatively use raw variant for escaped octets
                String schemePart = getParentSchemePart(modContainer.getURI().getSchemeSpecificPart());
                modContainer.setParent(allMods.get(schemePart));
            }
        }
        return allMods.values().stream();
    }

    private static List<? extends IModInfo> getNeoForgeModList() {
        if (ModList.get() != null) {
            return ModList.get().getMods();
        } else if (FMLLoader.getLoadingModList() != null) {
            return FMLLoader.getLoadingModList().getMods();
        } else {
            throw new NullPointerException("mod list is null");
        }
    }

    private static String getParentSchemePart(String schemePart) {
        // jar-in-jar mods can also be put outside META-INF, but this is the default place for NeoGradle & Architectury Loom
        return schemePart.replace("/jij:file:///", "file:///").replaceAll("_/META-INF/.+(#|%23)\\d+!/$", "!/");
    }
}
