package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.impl.event.DataPackFinderRegistryImpl;
import fuzs.puzzleslib.mixin.accessor.BuiltInPackSourceFabricAccessor;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PackRepository.class)
abstract class PackRepositoryFabricMixin {
    @Shadow
    @Final
    private Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(RepositorySource[] repositorySources, CallbackInfo callback) {
        // same implementation as Fabric Api to hook into server pack repository whenever a new one is created,
        // client resource packs can be handled much simpler since the repository is only ever created once
        boolean serverData = false;
        for (RepositorySource source : this.sources) {
            if (source instanceof BuiltInPackSource) {
                if (((BuiltInPackSourceFabricAccessor) source).puzzleslib$getPackType() == PackType.SERVER_DATA) {
                    serverData = true;
                    break;
                }
            }
        }
        if (!serverData) return;
        DataPackFinderRegistryImpl.addAllRepositorySources(PackRepository.class.cast(this));
    }
}
