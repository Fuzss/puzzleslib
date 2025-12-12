package fuzs.puzzleslib.neoforge.impl.core;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.i18n.MavenVersionTranslator;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jspecify.annotations.Nullable;

import java.net.URL;
import java.nio.file.Path;
import java.util.*;
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
        if (this.metadata.getOwningFile().getFile().getContents().get(String.join("/", path)) != null) {
            return Optional.of(this.metadata.getOwningFile().getFile().getFilePath().resolve(Path.of("", path)));
        } else {
            return Optional.empty();
        }
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

    private void setParent(@Nullable NeoForgeModContainer parent) {
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

    public static Stream<? extends ModContainer> getNeoForgeModContainers() {
        return getNeoForgeModList().stream().map(NeoForgeModContainer::new);
    }

    private static List<? extends IModInfo> getNeoForgeModList() {
        if (ModList.get() != null) {
            return ModList.get().getMods();
        } else if (FMLLoader.getCurrentOrNull() != null) {
            return FMLLoader.getCurrent().getLoadingModList().getMods();
        } else {
            throw new NullPointerException("mod list is null");
        }
    }
}
