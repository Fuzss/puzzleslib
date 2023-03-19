package fuzs.puzzleslib.api.data.v1;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class AbstractLanguageProvider extends LanguageProvider {

    public AbstractLanguageProvider(PackOutput packOutput, String modId) {
        super(packOutput, modId, "en_us");
    }

    @Override
    protected abstract void addTranslations();

    public void add(CreativeModeTab tab, String value) {
        if (tab.getDisplayName().getContents() instanceof TranslatableContents contents) {
            this.add(contents.getKey(), value);
        }
        throw new UnsupportedOperationException("Cannot add language entry for tab %s".formatted(tab.getDisplayName().getString()));
    }

    public void add(Attribute entityAttribute, String value) {
        this.add(entityAttribute.getDescriptionId(), value);
    }

    public void add(StatType<?> statType, String value) {
        this.add(statType.getTranslationKey(), value);
    }

    public void add(Potion potion, String value) {
        String potionName = potion.getName("");
        this.add("item.minecraft.tipped_arrow.effect." + potionName, "Arrow of " + value);
        this.add("item.minecraft.potion.effect." + potionName, "Potion of " + value);
        this.add("item.minecraft.splash_potion.effect." + potionName, "Splash Potion of " + value);
        this.add("item.minecraft.lingering_potion.effect." + potionName, "Lingering Potion of " + value);
    }

    public void add(SoundEvent soundEvent, String value) {
        this.add("subtitles." + soundEvent.getLocation().getPath(), value);
    }

    public void add(ResourceLocation identifier, String value) {
        this.add(identifier.toLanguageKey(), value);
    }
}
