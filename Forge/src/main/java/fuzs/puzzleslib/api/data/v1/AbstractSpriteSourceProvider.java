package fuzs.puzzleslib.api.data.v1;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public abstract class AbstractSpriteSourceProvider extends SpriteSourceProvider {

    public AbstractSpriteSourceProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, fileHelper, modId);
    }

    @Override
    protected abstract void addSources();
}
