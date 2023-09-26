package fuzs.puzzleslib.impl;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.api.core.v1.context.BlockInteractionsContext;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.api.event.v1.entity.player.GrindstoneEvents;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.ClientboundSyncCapabilityMessage;
import fuzs.puzzleslib.impl.core.ClientboundModListMessage;
import fuzs.puzzleslib.impl.entity.ClientboundAddEntityDataMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;

import java.util.Iterator;
import java.util.Map;

/**
 * This has been separated from {@link PuzzlesLib} to prevent issues with static initialization when accessing constants in {@link PuzzlesLib} early.
 */
public class PuzzlesLibMod extends PuzzlesLib implements ModConstructor {
    public static final NetworkHandlerV3 NETWORK = NetworkHandlerV3.builder(MOD_ID)
            .registerSerializer(ClientboundAddEntityPacket.class, (friendlyByteBuf, clientboundAddEntityPacket) -> clientboundAddEntityPacket.write(friendlyByteBuf), ClientboundAddEntityPacket::new)
            .allAcceptVanillaOrMissing()
            .registerClientbound(ClientboundSyncCapabilityMessage.class)
            .registerClientbound(ClientboundAddEntityDataMessage.class)
            .registerClientbound(ClientboundModListMessage.class);

    @Override
    public void onConstructMod() {
        Item book = Items.ENCHANTED_BOOK;
        GrindstoneEvents.UPDATE.register((ItemStack topInput, ItemStack bottomInput, MutableValue<ItemStack> output, MutableInt experienceReward, Player player) -> {
            if (topInput.isEnchanted() && bottomInput.is(book)) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(topInput);
                ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(enchantments, itemStack);
                output.accept(itemStack);
            } else if (topInput.is(Items.ENCHANTED_BOOK) && bottomInput.is(book)) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(topInput);
                Iterator<Map.Entry<Enchantment, Integer>> iterator = enchantments.entrySet().iterator();
                if (!iterator.hasNext()) return EventResult.PASS;
                ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.setEnchantments(ImmutableMap.ofEntries(iterator.next()), itemStack);
                output.accept(itemStack);
            }
            experienceReward.accept(0);
            return EventResult.ALLOW;
        });
        GrindstoneEvents.USE.register((DefaultedValue<ItemStack> topInput, DefaultedValue<ItemStack> bottomInput, Player player) -> {
            if (topInput.get().isEnchanted() && bottomInput.get().is(book)) {
                topInput.accept(topInput.get().copy());
                topInput.get().removeTagKey(ItemStack.TAG_ENCH);
            } else if (topInput.get().is(Items.ENCHANTED_BOOK) && bottomInput.get().is(book)) {
                ItemStack itemStack = topInput.get().copy();
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
                Iterator<Map.Entry<Enchantment, Integer>> iterator = enchantments.entrySet().iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
                itemStack.removeTagKey(EnchantedBookItem.TAG_STORED_ENCHANTMENTS);
                if (enchantments.isEmpty()) {
                    CompoundTag tag = itemStack.getTag();
                    itemStack = new ItemStack(Items.BOOK, itemStack.getCount());
                    itemStack.setTag(tag);
                } else {
                    EnchantmentHelper.setEnchantments(enchantments, itemStack);
                }
                topInput.accept(itemStack);
            }
        });
    }

    @Override
    public void onRegisterBlockInteractions(BlockInteractionsContext context) {
        context.registerStrippable(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG);
        context.registerStrippable(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD);
        context.registerFlattenable(Blocks.DIRT, Blocks.DIRT_PATH);
        context.registerFlattenable(Blocks.DIRT_PATH, Blocks.FARMLAND);
        context.registerTillable(Blocks.FARMLAND, Blocks.DIRT_PATH);
        context.registerTillable(Blocks.DIRT, Blocks.FARMLAND);
    }

    @Override
    public void onRegisterBiomeModifications(BiomeModificationsContext context) {
        context.register(BiomeLoadingPhase.MODIFICATIONS, biomeLoadingContext -> biomeLoadingContext.is(Biomes.PLAINS), context1 -> {
            context1.mobSpawnSettings().clearSpawns(MobCategory.CREATURE);
            context1.mobSpawnSettings().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 100, 4, 4));
        });
    }

    @Override
    public ContentRegistrationFlags[] getContentRegistrationFlags() {
        return new ContentRegistrationFlags[]{ContentRegistrationFlags.BIOME_MODIFICATIONS};
    }
}
