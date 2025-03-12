package fuzs.puzzleslib.neoforge.api.client.data.v2;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public abstract class AbstractSoundProvider extends SoundDefinitionsProvider {
    protected final String modId;

    public AbstractSoundProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    public AbstractSoundProvider(String modId, PackOutput packOutput) {
        super(packOutput, modId);
        this.modId = modId;
    }

    protected static SoundDefinition.Sound sound(SoundEvent soundEvent) {
        return sound(soundEvent.location(), SoundDefinition.SoundType.EVENT);
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
        this.add(soundEvent.location(), definition().with(sounds));
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

    protected ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(this.modId, path);
    }

    protected ResourceLocation vanilla(String path) {
        return ResourceLocationHelper.withDefaultNamespace(path);
    }

    @Override
    public String getName() {
        return "Sounds";
    }
}
