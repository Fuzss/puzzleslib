package fuzs.puzzleslib.neoforge.mixin;

import fuzs.puzzleslib.neoforge.impl.event.NeoForgeLootTableModifyEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootDataManager.class)
abstract class LootDataManagerNeoForgeMixin {
    @Shadow
    private Map<LootDataId<?>, ?> elements;

    @Inject(method = "apply", at = @At("TAIL"))
    private void apply(Map<LootDataType<?>, Map<ResourceLocation, ?>> map, CallbackInfo callback) {
        for (Map.Entry<LootDataId<?>, ?> entry : this.elements.entrySet()) {
            if (entry.getKey().type() == LootDataType.TABLE) {
                NeoForge.EVENT_BUS.post(new NeoForgeLootTableModifyEvent(LootDataManager.class.cast(this), entry.getKey().location(), (LootTable) entry.getValue()));
            }
        }
    }
}
