package fuzs.puzzleslib.neoforge.api.data.v2.client;

import fuzs.puzzleslib.neoforge.api.data.v2.core.ForgeDataProviderContext;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public abstract class AbstractSoundDefinitionProvider extends SoundDefinitionsProvider {
    protected final String modId;

    public AbstractSoundDefinitionProvider(ForgeDataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getFileHelper());
    }

    public AbstractSoundDefinitionProvider(String modId, PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, modId, fileHelper);
        this.modId = modId;
    }

    protected static SoundDefinition.Sound sound(SoundEvent soundEvent) {
        return sound(soundEvent.getLocation(), SoundDefinition.SoundType.EVENT);
    }

    @Override
    public final void registerSounds() {
        this.addSoundDefinitions();
    }

    public abstract void addSoundDefinitions();

    protected void add(SoundEvent soundEvent, String... sounds) {
        SoundDefinition definition = definition();
        for (String sound : sounds) {
            definition.with(sound(sound));
        }
        this.add(soundEvent, definition);
    }

    protected void add(SoundEvent soundEvent, ResourceLocation... sounds) {
        SoundDefinition definition = definition();
        for (ResourceLocation sound : sounds) {
            definition.with(sound(sound));
        }
        this.add(soundEvent, definition);
    }

    protected void add(SoundEvent soundEvent, SoundEvent... soundEvents) {
        SoundDefinition definition = definition();
        for (SoundEvent vanillaSoundEvent : soundEvents) {
            definition.with(sound(vanillaSoundEvent));
        }
        this.add(soundEvent, definition);
    }

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
