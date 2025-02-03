package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.GameRulesFactory;
import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.impl.core.CommonFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import fuzs.puzzleslib.neoforge.impl.event.ForwardingLootPoolBuilder;
import fuzs.puzzleslib.neoforge.impl.event.ForwardingLootTableBuilder;
import fuzs.puzzleslib.neoforge.impl.attachment.NeoForgeDataAttachmentRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.data.NeoForgeTagAppender;
import fuzs.puzzleslib.neoforge.impl.event.NeoForgeEventInvokerRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeGameRulesFactory;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeRegistryFactory;
import fuzs.puzzleslib.neoforge.impl.item.NeoForgeToolTypeHelper;
import fuzs.puzzleslib.neoforge.impl.item.crafting.NeoForgeCombinedIngredients;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public final class NeoForgeFactories implements CommonFactories {

    @Override
    public void constructMod(String modId, ModConstructor modConstructor) {
        NeoForgeModConstructor.construct(modConstructor, modId);
    }

    @Override
    public ModContext getModContext(String modId) {
        return new NeoForgeModContext(modId);
    }

    @Override
    public ProxyImpl getClientProxy() {
        return new NeoForgeClientProxy();
    }

    @Override
    public ProxyImpl getServerProxy() {
        return new NeoForgeServerProxy();
    }

    @Override
    public RegistryFactory getRegistryFactory() {
        return new NeoForgeRegistryFactory();
    }

    @Override
    public GameRulesFactory getGameRulesFactory() {
        return new NeoForgeGameRulesFactory();
    }

    @Override
    public void registerLoadingHandlers() {
        NeoForgeEventInvokerRegistryImpl.registerLoadingHandlers();
    }

    @Override
    public void registerEventHandlers() {
        NeoForgeEventInvokerRegistryImpl.freezeModBusEvents();
        NeoForgeEventInvokerRegistryImpl.registerEventHandlers();
    }

    @Override
    public ToolTypeHelper getToolTypeHelper() {
        return new NeoForgeToolTypeHelper();
    }

    @Override
    public CombinedIngredients getCombinedIngredients() {
        return new NeoForgeCombinedIngredients();
    }

    @Override
    public <T> AbstractTagAppender<T> getTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        return new NeoForgeTagAppender<>(tagBuilder, keyExtractor);
    }

    @Override
    public DataAttachmentRegistryImpl getDataAttachmentRegistry() {
        return new NeoForgeDataAttachmentRegistryImpl();
    }

    @Override
    public void forEachPool(LootTable.Builder lootTable, Consumer<? super LootPool.Builder> consumer) {
        if (lootTable instanceof ForwardingLootTableBuilder) {
            for (LootPool lootPool : lootTable.build().pools) {
                consumer.accept(new ForwardingLootPoolBuilder(lootPool));
            }
        } else {
            throw new UnsupportedOperationException("Must be ForwardingLootTableBuilder");
        }
    }
}
