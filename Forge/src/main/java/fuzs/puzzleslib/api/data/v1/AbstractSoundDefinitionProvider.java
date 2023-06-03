package fuzs.puzzleslib.api.data.v1;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public abstract class AbstractSoundDefinitionProvider extends SoundDefinitionsProvider {
    private final String modId;

    public AbstractSoundDefinitionProvider(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, modId, fileHelper);
        this.modId = modId;
    }

    protected static SoundDefinition.Sound sound(SoundEvent soundEvent) {
        return sound(soundEvent.getLocation(), SoundDefinition.SoundType.EVENT);
    }

    @Override
    public abstract void registerSounds();

    protected void add(final SoundEvent soundEvent, final SoundDefinition.Sound... sounds) {
        this.add(soundEvent.getLocation(), definition().with(sounds));
    }

    @Override
    protected void add(final ResourceLocation soundEvent, final SoundDefinition definition) {
        super.add(soundEvent, definition.subtitle("subtitles." + soundEvent.getPath()));
    }

    protected ResourceLocation id(String path) {
        return new ResourceLocation(this.modId, path);
    }

    protected ResourceLocation vanilla(String path) {
        return new ResourceLocation(path);
    }
}
