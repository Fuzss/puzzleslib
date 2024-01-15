package fuzs.puzzleslib.neoforge.impl.core;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforge.common.util.MavenVersionStringHelper;
import net.neoforged.neoforgespi.language.IModInfo;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record NeoForgeModContainer(IModInfo metadata) implements ModContainer {

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
        return MavenVersionStringHelper.artifactVersionToString(this.metadata.getVersion());
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
        this.metadata.getConfig().<String>getConfigElement("displayURL").or(() -> this.metadata.getModURL().map(URL::toString)).ifPresent(s -> builder.put("homepage", s));
        if (this.metadata.getOwningFile() instanceof ModFileInfo modFileInfo) {
            Optional.ofNullable(modFileInfo.getIssueURL()).map(URL::toString).ifPresent(s -> builder.put("issues", s));
        }
        return builder.build();
    }

    @Override
    public Optional<Path> findResource(String... path) {
        return Optional.of(this.metadata.getOwningFile().getFile().findResource(path)).filter(Files::exists);
    }
}
