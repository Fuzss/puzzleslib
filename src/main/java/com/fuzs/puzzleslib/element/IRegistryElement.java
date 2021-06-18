package com.fuzs.puzzleslib.element;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * an element which can store a name for itself
 * @param <T> element type
 */
public interface IRegistryElement<T> {

    /**
     * @param name name to set
     * @return this element
     */
    @Nonnull
    T setRegistryName(@Nonnull ResourceLocation name);

    /**
     * @return name of this as set in elements registry
     */
    @Nonnull
    ResourceLocation getRegistryName();

}
