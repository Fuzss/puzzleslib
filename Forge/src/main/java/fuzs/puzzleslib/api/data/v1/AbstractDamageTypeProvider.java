package fuzs.puzzleslib.api.data.v1;

import com.google.common.collect.Maps;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractDamageTypeProvider extends JsonCodecProvider<DamageType> {
    private final Map<ResourceLocation, DamageType> entries;

    public AbstractDamageTypeProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
        this(packOutput, modId, fileHelper, Maps.newHashMap());
    }

    private AbstractDamageTypeProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper, Map<ResourceLocation, DamageType> entries) {
        super(packOutput, fileHelper, modId, JsonOps.INSTANCE, PackType.SERVER_DATA, "damage_type", DamageType.CODEC, entries);
        this.entries = entries;
    }

    @Override
    protected final void gather(BiConsumer<ResourceLocation, DamageType> consumer) {
        this.addDamageSources();
        super.gather(consumer);
    }

    protected abstract void addDamageSources();

    protected void add(ResourceKey<DamageType> id, DamageType damageType) {
        this.add(id.location(), damageType);
    }

    protected void add(String id, DamageType damageType) {
        this.add(new ResourceLocation(this.modid, id), damageType);
    }

    protected void add(ResourceLocation id, DamageType damageType) {
        if (this.entries.put(id, damageType) != null) {
            throw new IllegalStateException("Damage type for " + id + " already registered");
        }
    }
}
