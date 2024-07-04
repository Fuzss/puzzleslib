package fuzs.puzzleslib.neoforge.mixin;

import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(AbstractPackResources.class)
abstract class AbstractPackResourcesNeoForgeMixin {

    @Inject(method = "getMetadataFromStream", at = @At("HEAD"), cancellable = true)
    private static <T> void getMetadataFromStream(MetadataSectionSerializer<T> deserializer, InputStream inputStream, CallbackInfoReturnable<T> callback) {
        // the deserializer always spams an error during data gen since pack.mcmeta has not been properly processed and still contain unexpanded strings
        if (!FMLEnvironment.production && DatagenModLoader.isRunningDataGen()) {
            callback.setReturnValue(null);
        }
    }
}
