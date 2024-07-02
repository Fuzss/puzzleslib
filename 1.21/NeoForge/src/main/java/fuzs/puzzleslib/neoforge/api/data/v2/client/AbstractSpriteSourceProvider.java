package fuzs.puzzleslib.neoforge.api.data.v2.client;

import fuzs.puzzleslib.neoforge.api.data.v2.core.ForgeDataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractSpriteSourceProvider extends SpriteSourceProvider {

    public AbstractSpriteSourceProvider(ForgeDataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries(), context.getFileHelper());
    }

    public AbstractSpriteSourceProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(packOutput, lookupProvider, modId, fileHelper);
    }

    @Override
    protected final void gather() {
        this.addSpriteSources();
    }

    public abstract void addSpriteSources();

    @Override
    public String getName() {
        return "Sprite Sources";
    }
}
