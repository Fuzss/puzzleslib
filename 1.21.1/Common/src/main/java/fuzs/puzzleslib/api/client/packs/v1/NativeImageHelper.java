package fuzs.puzzleslib.api.client.packs.v1;

import com.mojang.blaze3d.platform.NativeImage;

import java.io.IOException;

/**
 * A helper class containing legacy {@link NativeImage} methods.
 */
public final class NativeImageHelper {

    private NativeImageHelper() {
        // NO-OP
    }

    /**
     * Writes the native image data to a byte array.
     *
     * @param nativeImage the native image
     * @return the native image data as byte array
     *
     * @throws IOException thrown when writing fails
     */
    public static byte[] asByteArray(NativeImage nativeImage) throws IOException {
        return nativeImage.asByteArray();
    }
}
