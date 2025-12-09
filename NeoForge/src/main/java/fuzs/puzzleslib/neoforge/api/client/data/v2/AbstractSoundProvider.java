package fuzs.puzzleslib.neoforge.api.client.data.v2;

import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public abstract class AbstractSoundProvider extends SoundDefinitionsProvider {

    public AbstractSoundProvider(NeoForgeDataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getFileHelper());
    }

    public AbstractSoundProvider(String modId, PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, modId, fileHelper);
    }

    protected static SoundDefinition.Sound sound(SoundEvent soundEvent) {
        return sound(soundEvent.getLocation(), SoundDefinition.SoundType.EVENT);
    }

    @Override
    public final void registerSounds() {
        this.addSounds();
    }

    public abstract void addSounds();

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

    protected void add(SoundEvent soundEvent, SoundDefinition.Sound... sounds) {
        this.add(soundEvent.getLocation(), definition().with(sounds));
    }

    protected void addRecord(Holder<SoundEvent> soundEvent) {
        ResourceLocation resourceLocation = soundEvent.unwrap().orThrow().location().withPrefix("records/");
        SoundDefinition soundDefinition = definition().with(sound(resourceLocation).stream());
        this.add(soundEvent.value(), soundDefinition);
        soundDefinition.subtitle(null);
    }

    @Override
    protected void add(ResourceLocation soundEvent, SoundDefinition definition) {
        super.add(soundEvent, definition.subtitle("subtitles." + soundEvent.getPath()));
    }

    @Override
    public String getName() {
        return "Sounds";
    }
}
