package fuzs.puzzleslib.core;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * wrapper class for {@link ModConstructor} for calling all required registration methods at the correct time
 * most things need events for registering
 *
 * we use this wrapper style to allow for already registered to be used within the registration methods instead of having to use suppliers
 */
public class ForgeModConstructor {
    /**
     * mod base class
     */
    private final ModConstructor constructor;
    /**
     * stored burn times
     */
    private final Object2IntOpenHashMap<Item> fuelBurnTimes = new Object2IntOpenHashMap<>();

    /**
     * only calls {@link ModConstructor#onConstructMod()}, everything else is done via events later
     *
     * @param constructor mod base class
     */
    private ForgeModConstructor(ModConstructor constructor) {
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

    /**
     * helper method for registering fuel item burn time
     *
     * @param item     the item
     * @param burnTime burn time in ticks
     */
    private void registerFuelItem(Item item, int burnTime) {
        if (burnTime > 0 && item != null) this.fuelBurnTimes.put(item, burnTime);
    }

    /**
     * event for setting burn time, Forge wants this to be implemented on the item using {@link net.minecraftforge.common.extensions.IForgeItem#getBurnTime},
     * but this isn't very nice for instances of {@link net.minecraft.world.item.BlockItem}, so we do this installed
     *
     * @param evt the Forge event
     */
    private void onFurnaceFuelBurnTime(final FurnaceFuelBurnTimeEvent evt) {
        Item item = evt.getItemStack().getItem();
        if (this.fuelBurnTimes.containsKey(item)) {
            evt.setBurnTime(this.fuelBurnTimes.getInt(item));
        }
    }

    /**
     * construct the mod, calling all necessary registration methods
     * we don't need the object, it's only important for being registered to the necessary events buses
     *
     * @param constructor mod base class
     */
    public static void construct(ModConstructor constructor) {
        ForgeModConstructor forgeModConstructor = new ForgeModConstructor(constructor);
        FMLJavaModLoadingContext.get().getModEventBus().register(forgeModConstructor);
        // we need to manually register events for the normal event bus
        // as you cannot have both event bus types going through SubscribeEvent annotated methods in the same class
        MinecraftForge.EVENT_BUS.addListener(forgeModConstructor::onFurnaceFuelBurnTime);
    }
}
