package fuzs.puzzleslib.api.data.v1;

import com.google.common.collect.Maps;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractDamageTypeProvider extends JsonCodecProvider<DamageType> {
    private final Map<ResourceLocation, DamageType> entries;
    private final ExistingFileHelper.ResourceType resourceType;

    @Deprecated(forRemoval = true)
    public AbstractDamageTypeProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
        this(packOutput, fileHelper, modId);
    }

    @Deprecated(forRemoval = true)
    public AbstractDamageTypeProvider(PackOutput packOutput, ExistingFileHelper fileHelper) {
        this(packOutput, fileHelper, "");
    }

    public AbstractDamageTypeProvider(GatherDataEvent evt, String modId) {
        this(evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId);
    }

    public AbstractDamageTypeProvider(PackOutput packOutput, ExistingFileHelper fileHelper, String modId) {
        this(packOutput, fileHelper, modId, Maps.newHashMap());
    }

    private AbstractDamageTypeProvider(PackOutput packOutput, ExistingFileHelper fileHelper, String modId, Map<ResourceLocation, DamageType> entries) {
        super(packOutput, fileHelper, modId, JsonOps.INSTANCE, PackType.SERVER_DATA, "damage_type", DamageType.CODEC, entries);
        this.entries = entries;
        this.resourceType = new ExistingFileHelper.ResourceType(this.packType, ".json", this.directory);
    }

    @Override
    protected final void gather(BiConsumer<ResourceLocation, DamageType> consumer) {
        this.addDamageSources();
        super.gather(consumer);
    }

    protected abstract void addDamageSources();

    protected void add(ResourceKey<DamageType> id) {
        this.add(id.location(), new DamageType(id.location().getPath(), 0.1F));
    }

    protected void add(ResourceKey<DamageType> id, DamageEffects damageEffects) {
        this.add(id.location(), new DamageType(id.location().getPath(), 0.1F, damageEffects));
    }

    protected void add(ResourceKey<DamageType> id, DamageType damageType) {
        this.add(id.location(), damageType);
    }

    @Deprecated(forRemoval = true)
    protected void add(String id, DamageType damageType) {
        this.add(new ResourceLocation(this.modid, id), damageType);
    }

    protected final void add(ResourceLocation id, DamageType damageType) {
        this.existingFileHelper.trackGenerated(id, this.resourceType);
        if (this.entries.put(id, damageType) != null) {
            throw new IllegalStateException("Damage type for " + id + " already registered");
        }
    }

    @Override
    public String getName() {
        return "Damage Types";
    }
}
