package fuzs.puzzleslib.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * a list helper for storing {@link ResourceLocation}
 * resource locations for mods currently not installed won't be added
 * supports both adding resource locations directly, and adding whole {@link IForgeRegistryEntry}
 */
public class LoadedLocationList extends ArrayList<String> {

    /**
     * add a bunch of registry entries
     * @param elements elements to add
     */
    public void add(IForgeRegistryEntry<?>... elements) {

        Stream.of(elements).map(IForgeRegistryEntry::getRegistryName).forEach(this::add);
    }

    /**
     * add a whole resgitry entry
     * @param element element to add
     */
    public void add(IForgeRegistryEntry<?> element) {

        this.add(element.getRegistryName());
    }

    /**
     * add strings in pairs of two so they can be combined into resource locations
     * @param elements elements to add
     */
    public void add(String... elements) {

        if (elements.length % 2 != 0) {

            throw new IllegalStateException("Odd number of elements, needs pairs of two for namespace and path");
        }

        for (int i = 0; i < elements.length; i++) {

            this.add(new ResourceLocation(elements[i], elements[++i]));
        }
    }

    /**
     * @param elements elements to add
     */
    public void add(ResourceLocation... elements) {

        Stream.of(elements).forEach(this::add);
    }

    /**
     * this is where everything ends up
     * @param element element to add
     */
    public void add(ResourceLocation element) {

        // ModList::get still null at this point
        if (FMLLoader.getLoadingModList() == null || FMLLoader.getLoadingModList().getModFileById(element.getNamespace()) != null) {

            this.add(element.toString());
        }
    }

}
