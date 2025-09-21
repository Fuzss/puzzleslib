package fuzs.puzzleslib.impl.content;

import com.google.common.collect.Sets;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

/**
 * @see net.minecraft.server.commands.data.EntityDataAccessor
 */
public class ItemDataAccessor implements DataAccessor {
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(entity -> Component.translatableEscape(
            "commands.enchant.failed.entity",
            entity));
    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(entity -> Component.translatableEscape(
            "commands.enchant.failed.itemless",
            entity));
    public static final Function<String, DataCommands.DataProvider> PROVIDER = (String argumentName) -> new DataCommands.DataProvider() {
        @Override
        public DataAccessor access(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            Entity entity = EntityArgument.getEntity(context, argumentName);
            if (!(entity instanceof LivingEntity livingEntity)) {
                throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
            }
            ItemStack itemStack = livingEntity.getMainHandItem();
            if (itemStack.isEmpty()) {
                throw ERROR_NO_ITEM.create(entity.getName().getString());
            }
            return new ItemDataAccessor(context.getSource().registryAccess(), itemStack);
        }

        @Override
        public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> builder, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> action) {
            return builder.then(Commands.literal("item")
                    .then(action.apply(Commands.argument(argumentName, EntityArgument.entity()))));
        }
    };
    private final RegistryAccess registryAccess;
    private final ItemStack itemStack;

    public ItemDataAccessor(RegistryAccess registryAccess, ItemStack itemStack) {
        this.registryAccess = registryAccess;
        this.itemStack = itemStack;
    }

    @Override
    public void setData(CompoundTag compoundTag) throws CommandSyntaxException {
        RegistryOps<Tag> registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
        DataComponentMap.CODEC.parse(registryOps, compoundTag)
                .resultOrPartial()
                .ifPresent((DataComponentMap newComponents) -> {
                    DataComponentMap oldComponents = this.itemStack.getComponents();
                    this.itemStack.applyComponents(this.constructDataComponentPatch(oldComponents, newComponents));
                });
    }

    <T> DataComponentPatch constructDataComponentPatch(DataComponentMap oldComponents, DataComponentMap newComponents) {
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        for (DataComponentType<?> dataComponentType : Sets.union(oldComponents.keySet(), newComponents.keySet())) {
            T t = (T) newComponents.get(dataComponentType);
            if (!newComponents.has(dataComponentType)) {
                builder.remove(dataComponentType);
            } else if (!Objects.equals(oldComponents.get(dataComponentType), t)) {
                builder.set((DataComponentType<T>) dataComponentType, t);
            }
        }
        return builder.build();
    }

    @Override
    public CompoundTag getData() {
        RegistryOps<Tag> registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
        return DataComponentMap.CODEC.encodeStart(registryOps, this.itemStack.getComponents())
                .resultOrPartial(PuzzlesLib.LOGGER::error)
                .map((Tag tag) -> tag instanceof CompoundTag compoundTag ? compoundTag : null)
                .orElseGet(CompoundTag::new);
    }

    @Override
    public Component getModifiedSuccess() {
        return Component.translatable("commands.data.entity.modified", this.itemStack.getDisplayName());
    }

    @Override
    public Component getPrintSuccess(Tag tag) {
        return Component.translatable("commands.data.entity.query",
                this.itemStack.getDisplayName(),
                NbtUtils.toPrettyComponent(tag));
    }

    @Override
    public Component getPrintSuccess(NbtPathArgument.NbtPath path, double scale, int value) {
        return Component.translatable("commands.data.entity.get",
                path.asString(),
                this.itemStack.getDisplayName(),
                String.format(Locale.ROOT, "%.2f", scale),
                value);
    }
}
