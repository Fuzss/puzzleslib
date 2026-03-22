package fuzs.puzzleslib.fabric.impl.event;

import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public interface FabricPlayerInteraction {
    FabricPlayerInteraction USE_BLOCK = new FabricPlayerInteraction() {
        @Override
        public boolean sendServerboundPacket(InteractionResult interactionResult) {
            return interactionResult != InteractionResult.SUCCESS;
        }

        @Override
        public void sendServerboundPacket(Player player, Level level, InteractionHand interactionHand, @Nullable Entity entity, @Nullable HitResult hitResult) {
            Objects.requireNonNull(hitResult, "hit result is null");
            FabricProxy.get()
                    .startClientPrediction(level,
                            (int id) -> new ServerboundUseItemOnPacket(interactionHand,
                                    (BlockHitResult) hitResult,
                                    id));
        }
    };
    FabricPlayerInteraction USE_ITEM = new FabricPlayerInteraction() {
        @Override
        public boolean sendServerboundPacket(InteractionResult interactionResult) {
            return interactionResult != InteractionResult.SUCCESS;
        }

        @Override
        public void sendServerboundPacket(Player player, Level level, InteractionHand interactionHand, @Nullable Entity entity, @Nullable HitResult hitResult) {
            // send the move packet like vanilla to ensure the position+view vectors are accurate
            MessageSender.broadcast(new ServerboundMovePlayerPacket.PosRot(player.getX(),
                    player.getY(),
                    player.getZ(),
                    player.getYRot(),
                    player.getXRot(),
                    player.onGround(),
                    player.horizontalCollision));
            // send the interaction packet to the server with a new sequentially assigned id
            FabricProxy.get()
                    .startClientPrediction(level,
                            (int id) -> new ServerboundUseItemPacket(interactionHand,
                                    id,
                                    player.getYRot(),
                                    player.getXRot()));
        }
    };
    FabricPlayerInteraction USE_ENTITY = new FabricPlayerInteraction() {
        @Override
        public boolean sendServerboundPacket(InteractionResult interactionResult) {
            // cancel Fabric Api fully, it sends the wrong packet for a successful interaction
            return true;
        }

        @Override
        public void sendServerboundPacket(Player player, Level level, InteractionHand interactionHand, @Nullable Entity entity, @Nullable HitResult hitResult) {
            Objects.requireNonNull(entity, "entity is null");
            MessageSender.broadcast(ServerboundInteractPacket.createInteractionPacket(entity,
                    player.isShiftKeyDown(),
                    interactionHand));
        }

        @Override
        public InteractionResult finalizeInteraction(InteractionResult interactionResult, Player player, InteractionHand interactionHand) {
            // Fabric Api usually does this for us, but since we always fail, it will not
            if (interactionResult instanceof InteractionResult.Success success) {
                if (success.swingSource() == InteractionResult.SwingSource.CLIENT) {
                    player.swing(interactionHand);
                }
            }

            return InteractionResult.FAIL;
        }
    };
    FabricPlayerInteraction USE_ENTITY_AT = new FabricPlayerInteraction() {
        @Override
        public boolean sendServerboundPacket(InteractionResult interactionResult) {
            return !interactionResult.consumesAction();
        }

        @Override
        public void sendServerboundPacket(Player player, Level level, InteractionHand interactionHand, @Nullable Entity entity, @Nullable HitResult hitResult) {
            Objects.requireNonNull(entity, "entity is null");
            Objects.requireNonNull(hitResult, "hit result is null");
            MessageSender.broadcast(ServerboundInteractPacket.createInteractionPacket(entity,
                    player.isShiftKeyDown(),
                    interactionHand,
                    hitResult.getLocation()));
        }
    };

    default InteractionResult getHandledInteractionResult(EventResultHolder<InteractionResult> eventResult, Player player, Level level, InteractionHand interactionHand, @Nullable Entity entity, @Nullable HitResult hitResult) {
        Optional<InteractionResult> optional = eventResult.getInterrupt();

        if (optional.isPresent()) {
            InteractionResult interactionResult = optional.get();

            if (level.isClientSide() && this.sendServerboundPacket(interactionResult)) {
                // this brings parity with Forge where the server is notified regardless of the returned InteractionResult,
                // as the Forge event runs after the server packet is sent
                this.sendServerboundPacket(player, level, interactionHand, entity, hitResult);
            }

            return this.finalizeInteraction(interactionResult, player, interactionHand);
        } else {
            return InteractionResult.PASS;
        }
    }

    boolean sendServerboundPacket(InteractionResult interactionResult);

    void sendServerboundPacket(Player player, Level level, InteractionHand interactionHand, @Nullable Entity entity, @Nullable HitResult hitResult);

    default InteractionResult finalizeInteraction(InteractionResult interactionResult, Player player, InteractionHand interactionHand) {
        // this is done for parity with Forge where InteractionResult#PASS can be cancelled,
        // while on Fabric it will mark the event as having done nothing
        // unfortunately this will prevent the off-hand from being processed (if fired for the main hand),
        // but it's the best we can do
        return interactionResult != InteractionResult.PASS ? interactionResult : InteractionResult.FAIL;
    }
}
