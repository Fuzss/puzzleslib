package fuzs.puzzleslib.neoforge.mixin.accessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockNeoForgeAccessor {

    @Invoker("setFlammable")
    void puzzleslib$setFlammable(Block block, int encouragement, int flammability);
}
