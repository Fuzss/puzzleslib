package fuzs.puzzleslib.impl.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Collection;

/**
 * An improved version of {@link net.minecraft.server.commands.EnchantCommand} allowing for removing and changing
 * enchantments, additionally featuring a slightly more convenient command syntax.
 */
public class ModEnchantCommand {
    public static final String KEY_REMOVE_SUCCESS_SINGLE = "commands.enchant.remove.success.single";
    public static final String KEY_REMOVE_SUCCESS_MULTIPLE = "commands.enchant.remove.success.multiple";
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(object -> Component.translatable(
            "commands.enchant.failed.entity",
            object));
    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(object -> Component.translatable(
            "commands.enchant.failed.itemless",
            object));
    private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType(object -> Component.translatable(
            "commands.enchant.failed.incompatible",
            object));
    private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(Component.translatable(
            "commands.enchant.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("enchant")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("enchantment",
                                        ResourceArgument.resource(context, Registries.ENCHANTMENT))
                                .executes(commandContext -> enchant(commandContext.getSource(),
                                        EntityArgument.getEntities(commandContext, "targets"),
                                        ResourceArgument.getEnchantment(commandContext, "enchantment")))
                                .then(
                                        // restrict this to 255, enchantment levels above 255 are not supported in vanilla and will be reset to that anyway
                                        // min of 0 wouldn't do anything in vanilla, but now we use it to remove enchantments
                                        Commands.argument("level", IntegerArgumentType.integer(0, 255))
                                                .executes(commandContext -> enchant(commandContext.getSource(),
                                                        EntityArgument.getEntities(commandContext, "targets"),
                                                        ResourceArgument.getEnchantment(commandContext, "enchantment"),
                                                        IntegerArgumentType.getInteger(commandContext, "level")))))));
    }

    private static int enchant(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, Holder<Enchantment> holder) throws CommandSyntaxException {
        return enchant(commandSourceStack, collection, holder, holder.value().getMaxLevel());
    }

    private static int enchant(CommandSourceStack commandSourceStack, Collection<? extends Entity> entities, Holder<Enchantment> enchantment, int level) throws CommandSyntaxException {

        // removed max level check (/effect command doesn't have it as well)
        // this should actually be restricted via the argument type, but doesn't seem to work reliably
        // so just throw the same exception the argument type would
        if (level > 255) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().create(level, 255);
        }

        int successCount = 0;
        for (Entity entity : entities) {

            if (entity instanceof LivingEntity livingEntity) {

                ItemStack itemStack = livingEntity.getMainHandItem();
                if (!itemStack.isEmpty()) {

                    ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);
                    if (level == 0 || (isBook(itemStack) || enchantment.value().canEnchant(itemStack)) &&
                            isEnchantmentCompatible(itemEnchantments, enchantment)) {

                        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(itemEnchantments);
                        if (mutable.getLevel(enchantment) != level) {
                            mutable.set(enchantment, level);
                        } else {
                            if (entities.size() == 1) {
                                throw ERROR_NOTHING_HAPPENED.create();
                            }
                        }

                        if (itemStack.is(Items.BOOK) && !mutable.keySet().isEmpty()) {
                            // if there are more than one book in the item stack they are deleted, but that is fine since cheats are enabled anyway
                            itemStack = itemStack.transmuteCopy(Items.ENCHANTED_BOOK, 1);
                        }

                        EnchantmentHelper.setEnchantments(itemStack, mutable.toImmutable());

                        if (itemStack.is(Items.ENCHANTED_BOOK) && mutable.keySet().isEmpty()) {
                            itemStack = itemStack.transmuteCopy(Items.BOOK, 1);
                        }

                        livingEntity.setItemInHand(InteractionHand.MAIN_HAND, itemStack);

                        ++successCount;
                    } else if (entities.size() == 1) {
                        throw ERROR_INCOMPATIBLE.create(itemStack.getItem().getName(itemStack).getString());
                    }
                } else if (entities.size() == 1) {
                    throw ERROR_NO_ITEM.create(livingEntity.getName().getString());
                }
            } else if (entities.size() == 1) {
                throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
            }
        }

        if (successCount == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
        } else {
            if (entities.size() == 1) {
                commandSourceStack.sendSuccess(() -> level > 0 ? Component.translatable(
                        "commands.enchant.success.single",
                        Enchantment.getFullname(enchantment, level),
                        entities.iterator().next().getDisplayName()) : Component.translatableWithFallback(
                        KEY_REMOVE_SUCCESS_SINGLE,
                        "Removed enchantment %s from %s's item",
                        getFullname(enchantment),
                        entities.iterator().next().getDisplayName()), true);
            } else {
                commandSourceStack.sendSuccess(() -> level > 0 ? Component.translatable(
                        "commands.enchant.success.multiple",
                        Enchantment.getFullname(enchantment, level),
                        entities.size()) : Component.translatableWithFallback(KEY_REMOVE_SUCCESS_MULTIPLE,
                        "Removed enchantment %s from %s entities",
                        getFullname(enchantment),
                        entities.size()), true);
            }

            return successCount;
        }
    }

    /**
     * Copied from {@link Enchantment#getFullname(Holder, int)} without an enchantment level added at the end.
     */
    private static Component getFullname(Holder<Enchantment> enchantment) {
        MutableComponent mutableComponent = enchantment.value().description().copy();
        if (enchantment.is(EnchantmentTags.CURSE)) {
            return mutableComponent.withStyle(ChatFormatting.RED);
        } else {
            return mutableComponent.withStyle(ChatFormatting.GRAY);
        }
    }

    private static boolean isBook(ItemStack itemStack) {
        return itemStack.is(Items.BOOK) || itemStack.is(Items.ENCHANTED_BOOK);
    }

    private static boolean isEnchantmentCompatible(ItemEnchantments itemEnchantments, Holder<Enchantment> enchantment) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(itemEnchantments);
        mutable.set(enchantment, 0);
        return EnchantmentHelper.isEnchantmentCompatible(mutable.keySet(), enchantment);
    }
}
