package fuzs.puzzleslib.fabric.mixin.accessor;

import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(PackRepository.class)
public interface PackRepositoryFabricAccessor {

    @Accessor("sources")
    Set<RepositorySource> puzzleslib$getSources();

    @Accessor("sources")
    @Mutable
    void puzzleslib$setSources(Set<RepositorySource> sources);
}
