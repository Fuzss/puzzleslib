package fuzs.puzzleslib.api.data.v1;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public abstract class AbstractSpriteSourceProvider extends SpriteSourceProvider {

    public AbstractSpriteSourceProvider(PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, fileHelper, "");
    }

    @Override
    protected abstract void addSources();

    @Override
    public String getName() {
        return "Sprite Sources";
    }
}
