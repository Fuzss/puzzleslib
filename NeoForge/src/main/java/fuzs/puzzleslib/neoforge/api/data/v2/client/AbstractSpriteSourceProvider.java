package fuzs.puzzleslib.neoforge.api.data.v2.client;

import fuzs.puzzleslib.neoforge.api.data.v2.core.ForgeDataProviderContext;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public abstract class AbstractSpriteSourceProvider extends SpriteSourceProvider {

    public AbstractSpriteSourceProvider(ForgeDataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getFileHelper());
    }

    public AbstractSpriteSourceProvider(String modId, PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, fileHelper, modId);
    }

    @Override
    protected abstract void addSources();

    @Override
    public String getName() {
        return "Sprite Sources";
    }
}
