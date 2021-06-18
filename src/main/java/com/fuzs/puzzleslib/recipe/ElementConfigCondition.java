package com.fuzs.puzzleslib.recipe;

import com.fuzs.puzzleslib.PuzzlesLib;
import com.fuzs.puzzleslib.element.ElementRegistry;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.Optional;

/**
 * a crafting recipe condition which depends on a custom config option
 */
public class ElementConfigCondition implements ICondition {

    /**
     * mod element the recipe belongs to
     */
    private final ResourceLocation element;
    /**
     * config path for option
     */
    private final String path;

    /**
     * @param element mod element
     * @param path config path
     */
    public ElementConfigCondition(ResourceLocation element, String path) {

        this.element = element;
        this.path = path;
    }

    @Override
    public ResourceLocation getID() {

        return new ResourceLocation(PuzzlesLib.MODID, "element_config");
    }

    @Override
    public boolean test() {

        Optional<Boolean> configValue = ElementRegistry.getConfigValue(this.element, this.path);
        return configValue.isPresent() && configValue.get();
    }

    /**
     * serialize our custom config condition
     */
    public static class Serializer implements IConditionSerializer<ElementConfigCondition> {

        @Override
        public void write(JsonObject json, ElementConfigCondition value) {

            json.addProperty("element", value.element.toString());
            json.addProperty("path", value.path);
        }

        @Override
        public ElementConfigCondition read(JsonObject json) {

            ResourceLocation element = new ResourceLocation(JSONUtils.getAsString(json, "element"));
            String path = JSONUtils.getAsString(json, "path");

            return new ElementConfigCondition(element, path);
        }

        @Override
        public ResourceLocation getID() {

            return new ResourceLocation(PuzzlesLib.MODID, "element_config");
        }

    }

}
