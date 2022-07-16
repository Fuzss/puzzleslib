package fuzs.puzzleslib.core;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ForgeRegistrationV2 {
    private final ModConstructor constructor;
    /**
     * stored burn times
     */
    private final Object2IntOpenHashMap<Item> fuelBurnTimes = new Object2IntOpenHashMap<>();

    private ForgeRegistrationV2(ModConstructor constructor) {
        this.constructor = constructor;
        constructor.onConstructMod();
    }

    @SubscribeEvent
    public void onCommonSetup(final FMLCommonSetupEvent evt) {
        this.constructor.onCommonSetup();
        this.constructor.onRegisterSpawnPlacements(SpawnPlacements::register);
        this.constructor.onRegisterFuelBurnTimes(this::registerFuelItem);
    }

    @SubscribeEvent
    public void onEntityAttributeCreation(final EntityAttributeCreationEvent evt) {
        this.constructor.onEntityAttributeCreation((EntityType<? extends LivingEntity> entity, AttributeSupplier.Builder builder) -> evt.put(entity, builder.build()));
    }

    @SubscribeEvent
    public void onEntityAttributeModification(final EntityAttributeModificationEvent evt) {
        this.constructor.onEntityAttributeModification(evt::add);
    }

    private void registerFuelItem(Item item, int burnTime) {
        if (burnTime > 0 && item != null) this.fuelBurnTimes.put(item, burnTime);
    }

    private void onFurnaceFuelBurnTime(final FurnaceFuelBurnTimeEvent evt) {
        Item item = evt.getItemStack().getItem();
        if (this.fuelBurnTimes.containsKey(item)) {
            evt.setBurnTime(this.fuelBurnTimes.getInt(item));
        }
    }

    public static void construct(ModConstructor constructor) {
        ForgeRegistrationV2 registrar = new ForgeRegistrationV2(constructor);
        FMLJavaModLoadingContext.get().getModEventBus().register(registrar);
        // we need to manually register events for the normal event bus
        // as you cannot have both event bus types going through SubscribeEvent annotated methods in the same class
        MinecraftForge.EVENT_BUS.addListener(registrar::onFurnaceFuelBurnTime);
    }
}
