package fuzs.puzzleslib.neoforge.api.data.v1;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import net.minecraftforge.data.event.GatherDataEvent;

public abstract class AbstractSpriteSourceProvider extends SpriteSourceProvider {

    public AbstractSpriteSourceProvider(GatherDataEvent evt, String modId) {
        this(evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId);
    }

    public AbstractSpriteSourceProvider(PackOutput packOutput, ExistingFileHelper fileHelper, String modId) {
        super(packOutput, fileHelper, modId);
    }

    @Deprecated(forRemoval = true)
    public AbstractSpriteSourceProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
        this(packOutput, fileHelper, modId);
    }

    @Deprecated(forRemoval = true)
    public AbstractSpriteSourceProvider(PackOutput packOutput, ExistingFileHelper fileHelper) {
        this(packOutput, fileHelper, "");
    }

    @Override
    protected abstract void addSources();

    @Override
    public String getName() {
        return "Sprite Sources";
    }
}
