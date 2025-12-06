package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.impl.event.DataPackFinderRegistryImpl;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PackRepository.class)
abstract class PackRepositoryFabricMixin {
    @Shadow
    public Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(RepositorySource[] repositorySources, CallbackInfo callback) {
        // same implementation as Fabric Api to hook into server pack repository whenever a new one is created,
        // client resource packs can be handled much simpler since the repository is only ever created once
        for (RepositorySource source : this.sources) {
            if (source instanceof ServerPacksSource) {
                DataPackFinderRegistryImpl.addAllRepositorySources(PackRepository.class.cast(this));
                return;
            }
        }
    }
}
