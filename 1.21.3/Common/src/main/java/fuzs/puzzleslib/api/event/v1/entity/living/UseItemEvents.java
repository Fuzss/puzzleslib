package fuzs.puzzleslib.api.event.v1.entity.living;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class UseItemEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<Tick> TICK = EventInvoker.lookup(Tick.class);
    public static final EventInvoker<Stop> STOP = EventInvoker.lookup(Stop.class);
    public static final EventInvoker<Finish> FINISH = EventInvoker.lookup(Finish.class);

    private UseItemEvents() {

    }

    @FunctionalInterface
    public interface Start {
        /**
         * Fired when an item starts being used in {@link LivingEntity#startUsingItem(InteractionHand)}.
         * <p>
         * Can be called to set a custom use duration for items, this way items that normally cannot be used for a
         * duration support that.
         *
         * @param livingEntity         the entity using the item
         * @param itemStack            the item stack being used
         * @param remainingUseDuration the duration in ticks the stack needs to be used until
         *                             {@link net.minecraft.world.item.Item#finishUsingItem(ItemStack, Level,
         *                             LivingEntity)} is called retrieved from
         *                             {@link net.minecraft.world.item.Item#getUseDuration(ItemStack, LivingEntity)}
         * @return {@link EventResult#INTERRUPT} to prevent the item from starting to be used, {@link EventResult#PASS}
         *         to allow the item to be used continuously, <code>remainingUseDuration</code> will be set as remaining
         *         use ticks
         */
        EventResult onUseItemStart(LivingEntity livingEntity, ItemStack itemStack, MutableInt remainingUseDuration);
    }

    @FunctionalInterface
    public interface Tick {
        /**
         * Fired every tick an entity is using an item.
         *
         * @param livingEntity         the entity using the item
         * @param itemStack            the item stack being used
         * @param remainingUseDuration the duration in ticks the stack has already been in use for
         * @return {@link EventResult#INTERRUPT} to immediately stop the item from being used, no methods that usually
         *         run on use release / finishing are called, {@link EventResult#PASS} to allow the item to continue
         *         ticking
         */
        EventResult onUseItemTick(LivingEntity livingEntity, ItemStack itemStack, MutableInt remainingUseDuration);
    }

    @FunctionalInterface
    public interface Stop {
        /**
         * Fired when an item is stopped being used without being finished, meaning
         * {@link net.minecraft.world.item.Item#getUseDuration(ItemStack, LivingEntity)} has not been reached.
         * <p>
         * {@link net.minecraft.world.item.Item#releaseUsing(ItemStack, Level, LivingEntity, int)} is called for item
         * specific behavior to apply, like shooting bows, crossbows and tridents.
         * <p>
         * This event is called for items that do not utilise
         * {@link net.minecraft.world.item.Item#finishUsingItem(ItemStack, Level, LivingEntity)}.
         * <p>
         * Use items that have been stopped due to the held item stack changing do not receive this event.
         *
         * @param livingEntity         the entity using the item
         * @param itemStack            the item stack being used
         * @param remainingUseDuration the duration in ticks the stack has already been in use for
         * @return {@link EventResult#INTERRUPT} to prevent
         *         {@link net.minecraft.world.item.Item#releaseUsing(ItemStack, Level, LivingEntity, int)} from being
         *         called, directly proceeding to {@link LivingEntity#stopUsingItem()}, {@link EventResult#PASS} to
         *         allow the item to release normally by calling the dedicated vanilla method
         */
        EventResult onUseItemStop(LivingEntity livingEntity, ItemStack itemStack, int remainingUseDuration);
    }

    @FunctionalInterface
    public interface Finish {
        /**
         * Fired when an item is finished being used, meaning
         * {@link net.minecraft.world.item.Item#getUseDuration(ItemStack, LivingEntity)} has run out.
         * <p>
         * In that case {@link net.minecraft.world.item.Item#finishUsingItem(ItemStack, Level, LivingEntity)} is called
         * for applying effects to the user, such as restoring food points or applying mob effects.
         *
         * @param livingEntity         the entity using the item
         * @param itemStack            the item that is set to the entity's use hand after {@link LivingEntity#useItem}
         *                             has finished, like a bowl from eating stew, but most of the time when an item is
         *                             fully consumed like when eating normal food, {@link LivingEntity#useItem} is
         *                             simply returned and reduced by one item in the process
         * @param remainingUseDuration the duration in ticks the stack has already been in use for
         * @param originalItemStack    a copy of the item stack being used, made before using finished
         */
        void onUseItemFinish(LivingEntity livingEntity, MutableValue<ItemStack> itemStack, int remainingUseDuration, ItemStack originalItemStack);
    }
}
