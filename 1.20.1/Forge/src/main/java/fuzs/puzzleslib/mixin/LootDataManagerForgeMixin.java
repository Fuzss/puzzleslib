package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.impl.event.LootTableModifyEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootDataManager.class)
abstract class LootDataManagerForgeMixin {
    @Shadow
    private Map<LootDataId<?>, ?> elements;

    @Inject(method = "apply", at = @At("TAIL"))
    private void apply(Map<LootDataType<?>, Map<ResourceLocation, ?>> map, CallbackInfo callback) {
        for (Map.Entry<LootDataId<?>, ?> entry : this.elements.entrySet()) {
            if (entry.getKey().type() == LootDataType.TABLE) {
                MinecraftForge.EVENT_BUS.post(new LootTableModifyEvent(LootDataManager.class.cast(this), entry.getKey().location(), (LootTable) entry.getValue()));
            }
        }
    }
}
