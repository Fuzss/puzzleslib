package fuzs.puzzleslib.fabric.api.client.gui.v2;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.rendering.hud.HudElementRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.hud.HudLayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;

public final class HudStatusBarHeightRegistryImpl {
    /**
     * The height at which vanilla begins rendering status bars; this is used for health and food / mount health.
     */
    static final int DEFAULT_HEIGHT = 39;
    /**
     * The height at which the held item tooltip renders in vanilla; for our purposes we already subtract the default
     * height.
     */
    static final int HELD_ITEM_TOOLTIP_HEIGHT = 59 - DEFAULT_HEIGHT;
    /**
     * The height at which the overlay message (from playing records, or unsuccessfully trying to sleep) renders in
     * vanilla; for our purposes we already subtract the default height.
     */
    static final int OVERLAY_MESSAGE_HEIGHT = 68 - DEFAULT_HEIGHT;
    static final int TEXT_HEIGHT_DELTA = OVERLAY_MESSAGE_HEIGHT - HELD_ITEM_TOOLTIP_HEIGHT;
    static final ToIntFunction<Player> ZERO = (Player player) -> 0;
    /**
     * Height provider for the vanilla health bar.
     *
     * <p>Mods that would otherwise have a mixin for this are encouraged to instead register a replacement provider via
     * {@link HudStatusBarHeightRegistry#addLeft(ResourceLocation, ToIntFunction)}.
     */
    static final ToIntFunction<Player> HEALTH_BAR = (Player player) -> {
        Gui gui = Minecraft.getInstance().gui;
        int playerHealth = Mth.ceil(player.getHealth());
        int displayHealth = gui.displayHealth;
        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH),
                Math.max(displayHealth, playerHealth));
        int absorptionAmount = Mth.ceil(player.getAbsorptionAmount());
        int healthRows = Mth.ceil((maxHealth + absorptionAmount) / 2.0F / 10.0F);
        int rowShift = Math.max(10 - (healthRows - 2), 3);
        return 10 + (healthRows - 1) * rowShift;
    };
    /**
     * Height provider for the vanilla armor bar.
     *
     * <p>Mods that would otherwise have a mixin for this are encouraged to instead register a replacement provider via
     * {@link HudStatusBarHeightRegistry#addLeft(ResourceLocation, ToIntFunction)}.
     */
    static final ToIntFunction<Player> ARMOR_BAR = (Player player) -> {
        return player.getArmorValue() > 0 ? 10 : 0;
    };
    /**
     * Height provider for the vanilla mount health.
     *
     * <p>Mods that would otherwise have a mixin for this are encouraged to instead register a replacement provider via
     * {@link HudStatusBarHeightRegistry#addRight(ResourceLocation, ToIntFunction)}.
     */
    static final ToIntFunction<Player> MOUNT_HEALTH = (Player player) -> {
        Gui gui = Minecraft.getInstance().gui;
        LivingEntity livingEntity = gui.getPlayerVehicleWithHealth();
        int vehicleMaxHearts = gui.getVehicleMaxHearts(livingEntity);
        return gui.getVisibleVehicleHeartRows(vehicleMaxHearts) * 10;
    };
    /**
     * Height provider for the vanilla food bar.
     *
     * <p>Mods that would otherwise have a mixin for this are encouraged to instead register a replacement provider via
     * {@link HudStatusBarHeightRegistry#addRight(ResourceLocation, ToIntFunction)}.
     */
    static final ToIntFunction<Player> FOOD_BAR = (Player player) -> {
        Gui gui = Minecraft.getInstance().gui;
        LivingEntity livingEntity = gui.getPlayerVehicleWithHealth();
        return gui.getVehicleMaxHearts(livingEntity) == 0 ? 10 : 0;
    };
    /**
     * Height provider for the vanilla air bar.
     *
     * <p>Mods that would otherwise have a mixin for this are encouraged to instead register a replacement provider via
     * {@link HudStatusBarHeightRegistry#addRight(ResourceLocation, ToIntFunction)}.
     */
    static final ToIntFunction<Player> AIR_BAR = (Player player) -> {
        int maxAirSupply = player.getMaxAirSupply();
        int airSupply = Math.clamp(player.getAirSupply(), 0, maxAirSupply);
        boolean isInWater = player.isEyeInFluid(FluidTags.WATER);
        return isInWater || airSupply < maxAirSupply ? 10 : 0;
    };
    /**
     * This serves two purposes: it provides a fixed order for some vanilla status bars; and it provides reduced vanilla
     * height providers, to compare with the actual height providers during rendering for potential translations for
     * vanilla status bars. Translations are achieved via matrix stack transformations; alternatively can also be
     * implemented via mixins.
     *
     * <p>Do not use {@link Map#of()}; it does not preserve insertion order.
     */
    static final Map<ResourceLocation, ToIntFunction<Player>> VANILLA_HEIGHT_PROVIDERS = ImmutableMap.of(
            VanillaHudElements.HEALTH_BAR,
            ZERO,
            VanillaHudElements.ARMOR_BAR,
            HEALTH_BAR,
            VanillaHudElements.MOUNT_HEALTH,
            ZERO,
            VanillaHudElements.FOOD_BAR,
            ZERO,
            VanillaHudElements.AIR_BAR,
            reduceToIntFunctions(MOUNT_HEALTH, FOOD_BAR, Integer::sum));
    /**
     * Height providers registered for the left side above the hotbar.
     *
     * <p>Used for checking if any custom height providers have been registered to potentially skip resolving later on.
     */
    static final Map<ResourceLocation, ToIntFunction<Player>> VANILLA_LEFT_HEIGHT_PROVIDERS = ImmutableMap.of(
            VanillaHudElements.HEALTH_BAR,
            HEALTH_BAR,
            VanillaHudElements.ARMOR_BAR,
            ARMOR_BAR);
    /**
     * Height providers registered for the right side above the hotbar.
     *
     * <p>Used for checking if any custom height providers have been registered to potentially skip resolving later on.
     */
    static final Map<ResourceLocation, ToIntFunction<Player>> VANILLA_RIGHT_HEIGHT_PROVIDERS = ImmutableMap.of(
            VanillaHudElements.MOUNT_HEALTH,
            MOUNT_HEALTH,
            VanillaHudElements.FOOD_BAR,
            FOOD_BAR,
            VanillaHudElements.AIR_BAR,
            AIR_BAR);
    /**
     * Height providers registered for the left side above the hotbar, like health and armor.
     *
     * <p>The height providers registered here simply return the height of the corresponding status bar.
     */
    static final Map<ResourceLocation, ToIntFunction<Player>> LEFT_HEIGHT_PROVIDERS = new HashMap<>(
            VANILLA_LEFT_HEIGHT_PROVIDERS);
    /**
     * Height providers registered for the right side above the hotbar, like food and air bubbles.
     *
     * <p>The height providers registered here simply return the height of the corresponding status bar.
     */
    static final Map<ResourceLocation, ToIntFunction<Player>> RIGHT_HEIGHT_PROVIDERS = new HashMap<>(
            VANILLA_RIGHT_HEIGHT_PROVIDERS);

    /**
     * Height providers used during rendering computed from everything that was registered.
     *
     * <p>These providers do NOT
     * return the heights of individual elements; instead they return the height at which an element should render at,
     * which is computed by summing all the heights from providers considered "below" an element.
     */
    @Nullable
    static Map<ResourceLocation, ToIntFunction<Player>> resolvedHeightProviders;

    private HudStatusBarHeightRegistryImpl() {
        // NO-OP
    }

    public static void addLeft(ResourceLocation id, ToIntFunction<Player> heightProvider) {
        if (resolvedHeightProviders == null) {
            LEFT_HEIGHT_PROVIDERS.put(id, heightProvider);
        } else {
            throw new IllegalStateException("Height provider registry already frozen!");
        }
    }

    public static void addRight(ResourceLocation id, ToIntFunction<Player> heightProvider) {
        if (resolvedHeightProviders == null) {
            RIGHT_HEIGHT_PROVIDERS.put(id, heightProvider);
        } else {
            throw new IllegalStateException("Height provider registry already frozen!");
        }
    }

    public static int getHeight(ResourceLocation id) {
        if (resolvedHeightProviders == null) {
            throw new IllegalStateException("Trying to get status bar height for " + id + " too early");
        }

        if (!resolvedHeightProviders.containsKey(id)) {
            throw new IllegalArgumentException("Unknown status bar: " + id);
        }

        Player player = Minecraft.getInstance().gui.getCameraPlayer();

        if (player == null) {
            throw new IllegalStateException("Trying to get status bar height for " + id + " without a camera player");
        }

        return DEFAULT_HEIGHT + resolvedHeightProviders.get(id).applyAsInt(player);
    }

    public static void init() {
        ImmutableMap.Builder<ResourceLocation, ToIntFunction<Player>> builder = ImmutableMap.builder();
        ToIntFunction<Player> maxLeftHeightProvider = resolveHeightProviders(LEFT_HEIGHT_PROVIDERS, builder::put);
        ToIntFunction<Player> maxRightHeightProvider = resolveHeightProviders(RIGHT_HEIGHT_PROVIDERS, builder::put);
        resolvedHeightProviders = builder.build();
        applyVanillaHeightProviders(resolvedHeightProviders,
                reduceToIntFunctions(maxLeftHeightProvider, maxRightHeightProvider, Math::max));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static List<ResourceLocation> getOrderedHeightProviders(Map<ResourceLocation, ToIntFunction<Player>> heightProviderLookup) {
        // creates an ordered list of all height provider identifiers from the lookup,
        // with a fixed order provided for some vanilla elements and other elements attached to those via the static map;
        // all other elements are simply appended in the order they appear in the hud element registry
        List<ResourceLocation> orderedHeightProviders = new ArrayList<>();
        for (ResourceLocation resourceLocation : VANILLA_HEIGHT_PROVIDERS.keySet()) {
            for (HudLayer hudLayer : HudElementRegistryImpl.ROOT_ELEMENTS.get(resourceLocation).layers()) {
                if (!hudLayer.isRemoved() && heightProviderLookup.containsKey(hudLayer.id())) {
                    orderedHeightProviders.add(hudLayer.id());
                }
            }
        }
        for (Map.Entry<ResourceLocation, HudElementRegistryImpl.RootLayer> entry : HudElementRegistryImpl.ROOT_ELEMENTS.entrySet()) {
            if (!VANILLA_HEIGHT_PROVIDERS.containsKey(entry.getKey())) {
                for (HudLayer hudLayer : entry.getValue().layers()) {
                    if (!hudLayer.isRemoved() && heightProviderLookup.containsKey(hudLayer.id())) {
                        orderedHeightProviders.add(hudLayer.id());
                    }
                }
            }
        }
        return orderedHeightProviders;
    }

    private static ToIntFunction<Player> resolveHeightProviders(Map<ResourceLocation, ToIntFunction<Player>> heightProviderLookup, BiConsumer<ResourceLocation, ToIntFunction<Player>> heightProviderConsumer) {
        // called individually for both status bar sides for combining all height providers with the ones below them
        // finally returns a provider for the total height of all providers on this side
        List<ResourceLocation> orderedHeightProviders = getOrderedHeightProviders(heightProviderLookup);
        for (ResourceLocation resourceLocation : heightProviderLookup.keySet()) {
            ToIntFunction<Player> heightProvider = resolveHeightProvider(resourceLocation,
                    heightProviderLookup,
                    orderedHeightProviders);
            heightProviderConsumer.accept(resourceLocation, heightProvider);
        }
        return resolveMaximumHeightProvider(orderedHeightProviders.getLast(),
                heightProviderLookup,
                orderedHeightProviders);
    }

    private static ToIntFunction<Player> resolveHeightProvider(ResourceLocation resourceLocation, Map<ResourceLocation, ToIntFunction<Player>> heightProviderLookup, List<ResourceLocation> orderedHeightProviders) {
        // combines all height providers "below" a hud element for determining the height at which it should render at
        ToIntFunction<Player> heightProvider = ZERO;
        for (ResourceLocation heightProviderLocation : orderedHeightProviders) {
            if (heightProviderLocation.equals(resourceLocation)) {
                return heightProvider;
            } else if (heightProviderLookup.containsKey(heightProviderLocation)) {
                heightProvider = reduceToIntFunctions(heightProvider,
                        heightProviderLookup.get(heightProviderLocation),
                        Integer::sum);
            }
        }
        throw new IllegalStateException();
    }

    private static ToIntFunction<Player> resolveMaximumHeightProvider(ResourceLocation resourceLocation, Map<ResourceLocation, ToIntFunction<Player>> heightProviderLookup, List<ResourceLocation> orderedHeightProviders) {
        // combines all height providers "below" and including a hud element
        ToIntFunction<Player> heightProvider = resolveHeightProvider(resourceLocation,
                heightProviderLookup,
                orderedHeightProviders);
        return reduceToIntFunctions(heightProviderLookup.get(resourceLocation), heightProvider, Integer::sum);
    }

    private static <T> ToIntFunction<T> reduceToIntFunctions(ToIntFunction<T> first, ToIntFunction<T> second, IntBinaryOperator operator) {
        return (T t) -> operator.applyAsInt(first.applyAsInt(t), second.applyAsInt(t));
    }

    private static void applyVanillaHeightProviders(Map<ResourceLocation, ToIntFunction<Player>> resolvedHeightProviders, ToIntFunction<Player> maxHeightProvider) {
        // wrap vanilla status bars with matrix stack transformations to implement potentially altered height values
        for (Map.Entry<ResourceLocation, ToIntFunction<Player>> entry : VANILLA_HEIGHT_PROVIDERS.entrySet()) {
            if (isVanillaHeightProvider(entry.getKey())) {
                ToIntFunction<Player> actualHeightProvider = resolvedHeightProviders.get(entry.getKey());
                ToIntFunction<Player> expectedHeightProvider = entry.getValue();
                replaceVanillaElement(entry.getKey(),
                        reduceToIntFunctions(expectedHeightProvider,
                                actualHeightProvider,
                                (int i1, int i2) -> i1 - i2));
            }
        }
        // offset text above hotbar depending on height values
        replaceVanillaElement(VanillaHudElements.HELD_ITEM_TOOLTIP,
                (Player player) -> HELD_ITEM_TOOLTIP_HEIGHT - Math.max(HELD_ITEM_TOOLTIP_HEIGHT,
                        maxHeightProvider.applyAsInt(player)));
        replaceVanillaElement(VanillaHudElements.OVERLAY_MESSAGE,
                (Player player) -> OVERLAY_MESSAGE_HEIGHT - Math.max(OVERLAY_MESSAGE_HEIGHT,
                        maxHeightProvider.applyAsInt(player) + TEXT_HEIGHT_DELTA));
    }

    private static boolean isVanillaHeightProvider(ResourceLocation resourceLocation) {
        if (LEFT_HEIGHT_PROVIDERS.containsKey(resourceLocation)
                && LEFT_HEIGHT_PROVIDERS.get(resourceLocation) == VANILLA_LEFT_HEIGHT_PROVIDERS.get(resourceLocation)) {
            return true;
        }

        if (RIGHT_HEIGHT_PROVIDERS.containsKey(resourceLocation) && RIGHT_HEIGHT_PROVIDERS.get(resourceLocation)
                == VANILLA_RIGHT_HEIGHT_PROVIDERS.get(resourceLocation)) {
            return true;
        }

        return false;
    }

    private static void replaceVanillaElement(ResourceLocation resourceLocation, ToIntFunction<Player> heightProvider) {
        HudElementRegistry.replaceElement(resourceLocation, (HudElement layer) -> {
            return (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                Player player = Minecraft.getInstance().gui.getCameraPlayer();
                int height = player != null ? heightProvider.applyAsInt(player) : 0;
                if (height != 0) {
                    guiGraphics.pose().pushMatrix();
                    guiGraphics.pose().translate(0.0F, height);
                }
                layer.render(guiGraphics, deltaTracker);
                if (height != 0) {
                    guiGraphics.pose().popMatrix();
                }
            };
        });
    }
}
