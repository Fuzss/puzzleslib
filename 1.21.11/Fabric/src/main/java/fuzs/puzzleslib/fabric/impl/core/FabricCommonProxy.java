package fuzs.puzzleslib.fabric.impl.core;

import com.google.common.base.Predicates;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.recipes.TransformingRecipeOutput;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.fabric.impl.attachment.FabricDataAttachmentRegistryImpl;
import fuzs.puzzleslib.fabric.impl.core.context.PayloadTypesContextFabricImpl;
import fuzs.puzzleslib.fabric.impl.data.FabricTagAppender;
import fuzs.puzzleslib.fabric.impl.event.FabricEventInvokerRegistryImpl;
import fuzs.puzzleslib.fabric.impl.event.SpawnReasonMob;
import fuzs.puzzleslib.fabric.impl.init.FabricRegistryFactory;
import fuzs.puzzleslib.fabric.impl.item.FabricToolTypeHelper;
import fuzs.puzzleslib.fabric.impl.item.crafting.FabricCombinedIngredients;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.resource.pack.FabricPack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class FabricCommonProxy implements FabricProxy {
    private MinecraftServer minecraftServer;

    @Override
    public MinecraftServer getMinecraftServer() {
        return this.minecraftServer;
    }

    @Override
    public <T> void openMenu(Player player, MenuProvider menuProvider, T data) {
        player.openMenu(new ExtendedScreenHandlerFactory<>() {
            @Override
            public T getScreenOpeningData(ServerPlayer serverPlayer) {
                return data;
            }

            @Override
            public Component getDisplayName() {
                return menuProvider.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return menuProvider.createMenu(containerId, inventory, player);
            }
        });
    }

    @Override
    public Pack.Metadata createPackInfo(Identifier identifier, Component descriptionComponent, PackCompatibility packCompatibility, FeatureFlagSet featureFlagSet, boolean isHidden) {
        return new Pack.Metadata(descriptionComponent, packCompatibility, featureFlagSet, Collections.emptyList());
    }

    @Override
    public boolean isPackHidden(Pack pack) {
        try {
            // Fabric already has all the infrastructure for this flag implemented (which is quite a lot!), so we use it despite it being internal.
            return ((FabricPack) pack).fabric$isHidden();
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public void setPackHidden(Pack pack, boolean isHidden) {
        if (isHidden) {
            try {
                // Fabric Api checks this using reference equality against an internally stored field when a pack is not supposed to be hidden.
                // We do not have access to that field, so we only support making the pack hidden, which is fine.
                ((FabricPack) pack).fabric$setParentsPredicate(Predicates.alwaysTrue());
            } catch (Throwable throwable) {
                // NO-OP
            }
        } else {
            throw new IllegalArgumentException("unable to reveal pack");
        }
    }

    @Override
    public Style getRarityStyle(Rarity rarity) {
        return Style.EMPTY.applyFormat(rarity.color());
    }

    @Override
    public void onPlayerDestroyItem(Player player, ItemStack originalItemStack, @Nullable InteractionHand interactionHand) {
        // NO-OP
    }

    @Override
    public void forEachPool(LootTable.Builder lootTable, Consumer<? super LootPool.Builder> lootPoolConsumer) {
        lootTable.modifyPools(lootPoolConsumer);
    }

    @Override
    public float getEnchantPowerBonus(BlockState blockState, Level level, BlockPos blockPos) {
        return blockState.is(BlockTags.ENCHANTMENT_POWER_PROVIDER) ? 1.0F : 0.0F;
    }

    @Override
    public boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack) {
        return itemStack.canBeEnchantedWith(enchantment, EnchantingContext.PRIMARY);
    }

    @MustBeInvokedByOverriders
    @Override
    public void registerAllLoadingHandlers() {
        FabricEventInvokerRegistryImpl.registerLoadingHandlers();
    }

    @MustBeInvokedByOverriders
    @Override
    public void registerAllEventHandlers() {
        FabricEventInvokerRegistryImpl.registerEventHandlers();
    }

    @MustBeInvokedByOverriders
    @Override
    public boolean hasChannel(PacketListener packetListener, CustomPacketPayload.Type<?> type) {
        if (packetListener instanceof ServerConfigurationPacketListenerImpl serverConfigurationPacketListener) {
            return ServerConfigurationNetworking.canSend(serverConfigurationPacketListener, type);
        } else if (packetListener instanceof ServerGamePacketListenerImpl serverGamePacketListener) {
            return ServerPlayNetworking.canSend(serverGamePacketListener, type);
        } else {
            return false;
        }
    }

    @MustBeInvokedByOverriders
    @Override
    public Connection getConnection(PacketListener packetListener) {
        return packetListener instanceof ServerCommonPacketListenerImpl serverPacketListener ?
                serverPacketListener.connection : null;
    }

    @Override
    public Packet<ClientCommonPacketListener> toClientboundPacket(CustomPacketPayload payload) {
        return ServerPlayNetworking.createS2CPacket(payload);
    }

    @Override
    public Packet<ServerCommonPacketListener> toServerboundPacket(CustomPacketPayload payload) {
        return ClientPlayNetworking.createC2SPacket(payload);
    }

    @Override
    public void finishConfigurationTask(ServerConfigurationPacketListener packetListener, ConfigurationTask.Type type) {
        ((ServerConfigurationPacketListenerImpl) packetListener).completeTask(type);
    }

    @Override
    public PayloadTypesContext createPayloadTypesContext(String modId) {
        return new PayloadTypesContextFabricImpl.ServerImpl(modId);
    }

    @MustBeInvokedByOverriders
    @Override
    public void setupHandshakePayload(CustomPacketPayload.Type<BrandPayload> payloadType) {
        ServerPlayNetworking.registerGlobalReceiver(payloadType,
                (BrandPayload payload, ServerPlayNetworking.Context context) -> {
                    // NO-OP
                });
    }

    @Override
    public ModConstructorImpl<ModConstructor> getModConstructorImpl() {
        return new FabricModConstructor();
    }

    @Override
    public ModContext getModContext(String modId) {
        return new FabricModContext(modId);
    }

    @Override
    public RegistryFactory getRegistryFactory() {
        return new FabricRegistryFactory();
    }

    @Override
    public ToolTypeHelper getToolTypeHelper() {
        return new FabricToolTypeHelper();
    }

    @Override
    public CombinedIngredients getCombinedIngredients() {
        return new FabricCombinedIngredients();
    }

    @Override
    public <T> AbstractTagAppender<T> getTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        return new FabricTagAppender<>(tagBuilder, keyExtractor);
    }

    @Override
    public DataAttachmentRegistryImpl getDataAttachmentRegistry() {
        return new FabricDataAttachmentRegistryImpl();
    }

    @Override
    public RecipeOutput getTransformingRecipeOutput(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator) {
        return new TransformingRecipeOutput() {
            @Override
            public RecipeOutput recipeOutput() {
                return recipeOutput;
            }

            @Override
            public UnaryOperator<Recipe<?>> operator() {
                return operator;
            }
        };
    }

    @Override
    public RecipeOutput getRecipeProviderOutput(CachedOutput output, String modId, PackOutput packOutput, HolderLookup.Provider registries, Consumer<CompletableFuture<?>> consumer) {
        return new AbstractRecipeProvider.RecipeOutputImpl(output, modId, packOutput, registries, consumer) {
            // NO-OP
        };
    }

    @Override
    public RecipeOutput getThrowingRecipeOutput() {
        return new RecipeOutput() {
            @Override
            public void accept(ResourceKey<Recipe<?>> resourceKey, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Advancement.Builder advancement() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void includeRootAdvancement() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean canEquip(ItemStack itemStack, EquipmentSlot equipmentSlot, LivingEntity livingEntity) {
        return equipmentSlot == livingEntity.getEquipmentSlotForItem(itemStack);
    }

    @Override
    public @Nullable EntitySpawnReason getMobSpawnReason(Mob mob) {
        return ((SpawnReasonMob) mob).puzzleslib$getSpawnReason();
    }

    @Override
    public boolean isMobGriefingAllowed(ServerLevel serverLevel, @Nullable Entity entity) {
        return serverLevel.getGameRules().get(GameRules.MOB_GRIEFING);
    }

    @Override
    public Entity getPartEntityParent(Entity entity) {
        return entity instanceof EnderDragonPart enderDragonPart ? enderDragonPart.parentMob : entity;
    }

    @Override
    public boolean isFakePlayer(ServerPlayer serverPlayer) {
        return serverPlayer instanceof FakePlayer;
    }

    @Override
    public boolean isPiglinCurrency(ItemStack itemStack) {
        return itemStack.is(PiglinAi.BARTERING_ITEM);
    }

    @MustBeInvokedByOverriders
    @Override
    public void registerEventHandlers() {
        FabricProxy.super.registerEventHandlers();
        // registers for game server starting and stopping, so we can keep an instance of the server here
        ServerLifecycleEvents.STARTING.register(EventPhase.FIRST, (MinecraftServer minecraftServer) -> {
            this.minecraftServer = minecraftServer;
        });
        ServerLifecycleEvents.STOPPED.register(EventPhase.LAST, (MinecraftServer minecraftServer) -> {
            this.minecraftServer = null;
        });
    }
}
