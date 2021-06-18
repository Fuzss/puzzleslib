package com.fuzs.puzzleslib.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * client proxy class
 */
public class ClientProxy implements IProxy<Minecraft> {

    @Override
    public Minecraft getInstance() {

        return LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public PlayerEntity getPlayer(@Nullable PlayerEntity player) {

        return player != null ? player : this.getInstance().player;
    }

}
