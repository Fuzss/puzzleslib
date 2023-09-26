package fuzs.puzzleslib.api.data.v2.client.model;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Map;

public abstract class UncheckedModelBuilder<T extends ModelBuilder<T>> extends ModelBuilder<T> {

    protected UncheckedModelBuilder(ResourceLocation outputLocation, ExistingFileHelper fileHelper) {
        super(outputLocation, fileHelper);
    }

    /**
     * Set the texture for a given dictionary key. <br>
     * This method won't check if a texture actually exists, allowing for data generation of models using dynamically generated textures such as armor trims.
     *
     * @param key     the texture key
     * @param texture the texture, can be another key e.g. {@code "#all"}
     * @return this builder
     * @throws NullPointerException  if {@code key} is {@code null}
     * @throws NullPointerException  if {@code texture} is {@code null}
     */
    public T uncheckedTexture(String key, String texture) {
        Preconditions.checkNotNull(key, "Key must not be null");
        Preconditions.checkNotNull(texture, "Texture must not be null");
        if (texture.charAt(0) == '#') {
            this.textures().put(key, texture);
            return (T) this;
        } else {
            ResourceLocation asLoc;
            if (texture.contains(":")) {
                asLoc = new ResourceLocation(texture);
            } else {
                asLoc = new ResourceLocation(this.getLocation().getNamespace(), texture);
            }
            return this.uncheckedTexture(key, asLoc);
        }
    }

    /**
     * Set the texture for a given dictionary key. <br>
     * This method won't check if a texture actually exists, allowing for data generation of models using dynamically generated textures such as armor trims.
     *
     * @param key     the texture key
     * @param texture the texture
     * @return this builder
     * @throws NullPointerException  if {@code key} is {@code null}
     * @throws NullPointerException  if {@code texture} is {@code null}
     */
    public T uncheckedTexture(String key, ResourceLocation texture) {
        Preconditions.checkNotNull(key, "Key must not be null");
        Preconditions.checkNotNull(texture, "Texture must not be null");
        this.textures().put(key, texture.toString());
        return (T) this;
    }

    protected abstract Map<String, String> textures();
}
