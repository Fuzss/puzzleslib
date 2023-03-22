package fuzs.puzzleslib.impl.event;

import com.google.common.collect.MapMaker;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.*;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.FabricEventInvokerRegistry;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingExperienceDropCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingFallCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.AnvilRepairCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.BonemealCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerXpEvents;
import fuzs.puzzleslib.api.event.v1.world.BlockEvents;
import fuzs.puzzleslib.impl.client.event.FabricClientEventInvokers;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.function.Function;

public class FabricEventInvokerRegistryImpl implements FabricEventInvokerRegistry {
    public static final FabricEventInvokerRegistryImpl INSTANCE = new FabricEventInvokerRegistryImpl();
    private static final Map<Class<?>, EventInvoker<?>> EVENT_INVOKER_LOOKUP = new MapMaker().weakKeys().makeMap();

    static {
        INSTANCE.register(PlayerInteractEvents.UseBlock.class, UseBlockCallback.EVENT, callback -> {
            return (player, world, hand, hitResult) -> callback.onUseBlock(player, world, hand, hitResult).getInterrupt().orElse(InteractionResult.PASS);
        });
        INSTANCE.register(PlayerInteractEvents.UseItem.class, UseItemCallback.EVENT, callback -> {
            return (player, level, hand) -> callback.onUseItem(player, level, hand).getInterrupt().orElse(InteractionResultHolder.pass(ItemStack.EMPTY));
        });
        INSTANCE.register(PlayerXpEvents.PickupXp.class, FabricPlayerEvents.PICKUP_XP);
        INSTANCE.register(BonemealCallback.class, FabricPlayerEvents.BONEMEAL);
        INSTANCE.register(LivingExperienceDropCallback.class, FabricLivingEvents.EXPERIENCE_DROP);
        INSTANCE.register(BlockEvents.FarmlandTrample.class, FabricWorldEvents.FARMLAND_TRAMPLE);
        INSTANCE.register(PlayerTickEvents.Start.class, FabricEvents.PLAYER_TICK_START);
        INSTANCE.register(PlayerTickEvents.End.class, FabricEvents.PLAYER_TICK_END);
        INSTANCE.register(LivingFallCallback.class, FabricLivingEvents.LIVING_FALL);
        INSTANCE.register(RegisterCommandsCallback.class, CommandRegistrationCallback.EVENT, callback -> {
            return callback::onRegisterCommands;
        });
        INSTANCE.register(LootTableLoadEvents.Replace.class, LootTableEvents.REPLACE, callback -> {
            return (ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable original, LootTableSource source) -> {
                // keep this the same as Forge where editing data pack specified loot tables is not supported
                if (source == LootTableSource.DATA_PACK) return null;
                DefaultedValue<LootTable> lootTable = DefaultedValue.fromValue(original);
                callback.onReplaceLootTable(lootManager, id, lootTable);
                // returning null will prompt no change
                return lootTable.getAsOptional().orElse(null);
            };
        });
        INSTANCE.register(LootTableLoadEvents.Modify.class, LootTableEvents.MODIFY, callback -> {
            return (ResourceManager resourceManager, LootTables lootManager, ResourceLocation id, LootTable.Builder tableBuilder, LootTableSource source) -> {
                // keep this the same as Forge where editing data pack specified loot tables is not supported
                if (source == LootTableSource.DATA_PACK) return;
                callback.onModifyLootTable(lootManager, id, tableBuilder::pool, index -> {
                    MutableInt currentIndex = new MutableInt();
                    MutableBoolean result = new MutableBoolean();
                    tableBuilder.modifyPools(builder -> {
                        if (index == currentIndex.getAndIncrement()) {
                            // there is no way in Fabric Api to remove loot pools, but this seems to work for disabling at least
                            builder.setRolls(ConstantValue.exactly(0.0F));
                            builder.setBonusRolls(ConstantValue.exactly(0.0F));
                            result.setTrue();
                        }
                    });
                    return result.booleanValue();
                });
            };
        });
        INSTANCE.register(AnvilRepairCallback.class, FabricPlayerEvents.ANVIL_REPAIR);
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            FabricClientEventInvokers.register();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> EventInvoker<T> lookup(Class<T> clazz) {
        Objects.requireNonNull(clazz, "type is null");
        EventInvoker<T> invoker = (EventInvoker<T>) EVENT_INVOKER_LOOKUP.get(clazz);
        Objects.requireNonNull(invoker, "invoker for type %s is null".formatted(clazz));
        return invoker;
    }

    @Override
    public <T, E> void register(Class<T> clazz, Event<E> event, Function<T, E> converter) {
        Objects.requireNonNull(clazz, "type is null");
        Objects.requireNonNull(event, "event is null");
        Objects.requireNonNull(converter, "converter is null");
        EventInvoker<T> invoker = new FabricEventInvoker<>(event, converter);
        if (EVENT_INVOKER_LOOKUP.put(clazz, invoker) != null) {
            throw new IllegalArgumentException("duplicate event invoker for type %s".formatted(clazz));
        }
    }

    private record FabricEventInvoker<T, E>(Event<E> event, Function<T, E> converter, Set<EventPhase> knownEventPhases) implements EventInvoker<T> {

        public FabricEventInvoker(Event<E> event, Function<T, E> converter) {
            this(event, converter, Collections.newSetFromMap(new MapMaker().weakKeys().makeMap()));
        }

        @Override
        public void register(EventPhase phase, T callback) {
            Objects.requireNonNull(phase, "phase is null");
            Objects.requireNonNull(callback, "callback is null");
            // this is the default phase
            if (phase.parent() == null) {
                this.event.register(this.converter.apply(callback));
            } else {
                // make sure phase has a phase ordering, we keep track of phases we have already added an ordering for in this event in #knownEventPhases
                this.testEventPhase(phase);
                this.event.register(phase.identifier(), this.converter.apply(callback));
            }
        }

        private void testEventPhase(EventPhase phase) {
            Stack<EventPhase> stack = new Stack<>();
            // find the first parent we already know (probably default)
            while (phase.parent() != null && !this.knownEventPhases.contains(phase)) {
                stack.push(phase);
                phase = phase.parent();
            }
            // add a phase ordering for all parents in reverse order until we reach the phase we want to add
            while (!stack.isEmpty()) {
                phase = stack.pop();
                phase.applyOrdering(this.event::addPhaseOrdering);
                this.knownEventPhases.add(phase);
            }
        }
    }
}
