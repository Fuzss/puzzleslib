package fuzs.puzzleslib.api.client.packs.v1;

import com.mojang.blaze3d.platform.NativeImage;
import org.lwjgl.stb.STBImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

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
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream)) {
            if (!nativeImage.writeToChannel(writableByteChannel)) {
                throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
            } else {
                return byteArrayOutputStream.toByteArray();
            }
        }
    }
}
