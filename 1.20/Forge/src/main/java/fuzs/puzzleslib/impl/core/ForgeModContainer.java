package fuzs.puzzleslib.impl.core;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ForgeModContainer(IModInfo metadata) implements ModContainer {

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
    public Optional<String> getIconPath(int size) {
        return Optional.ofNullable((String) this.metadata.getModProperties().get("catalogueImageIcon")).or(this.metadata::getLogoFile);
    }

    @Override
    public boolean isClientOnly() {
        // this is meant for server compatibility testing and has nothing to do whether a mod loads only client-side or not,
        // but it's the closest Forge has to marking a mod client-only
        return this.metadata.getConfig().<String>getConfigElement("displayTest").filter("IGNORE_ALL_VERSION"::equals).isPresent();
    }

    @Override
    public boolean isLibrary() {
        // Forge does not provide a way to mark mods as libraries
        return false;
    }

    @Override
    public Optional<Path> findResource(String... path) {
        return Optional.of(this.metadata.getOwningFile().getFile().findResource(path)).filter(Files::exists);
    }

    @Override
    public List<String> getDependencyIds() {
        return this.metadata.getDependencies().stream().filter(IModInfo.ModVersion::isMandatory).map(IModInfo.ModVersion::getModId).toList();
    }

    @Override
    public Optional<String> getUpdateUrl() {
        return this.metadata.getUpdateURL().map(URL::toString);
    }

    @Override
    public boolean isUpdateAvailable() {
        return VersionChecker.getResult(this.metadata).status().isAnimated();
    }
}
