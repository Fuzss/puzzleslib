package fuzs.puzzleslib.mixin;

import com.google.gson.JsonElement;
import fuzs.puzzleslib.impl.event.LootTableModifyEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootTables.class)
abstract class LootTablesForgeMixin {
    @Shadow
    private Map<ResourceLocation, LootTable> tables;

    @Inject(method = "apply", at = @At("TAIL"))
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo callback) {
        for (Map.Entry<ResourceLocation, LootTable> entry : this.tables.entrySet()) {
            MinecraftForge.EVENT_BUS.post(new LootTableModifyEvent(LootTables.class.cast(this), entry.getKey(), entry.getValue()));
        }
    }
}
