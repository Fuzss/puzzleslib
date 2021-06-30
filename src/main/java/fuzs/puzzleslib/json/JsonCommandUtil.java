package fuzs.puzzleslib.json;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.util.function.Consumer;

/**
 * create a command for reloading a json config file from in-game
 */
public class JsonCommandUtil {

    /**
     * @param jsonName name of file, will be used as translation key also
     * @param modId mod this command belongs to, will be used as main command name
     * @param serializer create new config if absent
     * @param deserializer read from file
     * @return new command
     */
    public static LiteralArgumentBuilder<CommandSource> createReloadCommand(String jsonName, String modId, Consumer<File> serializer, Consumer<FileReader> deserializer) {

        return createReloadCommand(jsonName, modId, "command.reload." + jsonName.replace(".json", ""), serializer, deserializer);
    }

    /**
     * @param jsonName name of file
     * @param modId mod this command belongs to, will be used as main command name
     * @param translationKey translation key for success message
     * @param serializer create new config if absent
     * @param deserializer read from file
     * @return new command
     */
    public static LiteralArgumentBuilder<CommandSource> createReloadCommand(String jsonName, String modId, String translationKey, Consumer<File> serializer, Consumer<FileReader> deserializer) {

        return Commands.literal(modId).then(Commands.literal("reload").executes(ctx -> {

            handleFileReload(ctx, jsonName, translationKey, serializer, deserializer);

            return Command.SINGLE_SUCCESS;
        }));
    }

    /**
     * @param ctx execution context
     * @param jsonName name of file
     * @param translationKey translation key for success message
     * @param serializer create new config if absent
     * @param deserializer read from file
     */
    public static void handleFileReload(CommandContext<CommandSource> ctx, String jsonName, String translationKey, Consumer<File> serializer, Consumer<FileReader> deserializer) {

        handleFolderReload(ctx, jsonName, null, translationKey, serializer, deserializer);
    }

    /**
     * @param ctx execution context
     * @param jsonName name of file
     * @param modId mod this command belongs to, will be used as main command name
     * @param translationKey translation key for success message
     * @param serializer create new config if absent
     * @param deserializer read from file
     */
    public static void handleFolderReload(CommandContext<CommandSource> ctx, String jsonName, @Nullable String modId, String translationKey, Consumer<File> serializer, Consumer<FileReader> deserializer) {

        reloadJsonConfig(jsonName, modId, serializer, deserializer);
        sendFeedback(ctx, jsonName, modId, translationKey);
    }

    /**
     * @param jsonName name of file
     * @param modId mod this command belongs to, will be used as main command name
     * @param serializer create new config if absent
     * @param deserializer read from file
     */
    private static void reloadJsonConfig(String jsonName, @Nullable String modId, Consumer<File> serializer, Consumer<FileReader> deserializer) {

        if (modId != null) {

            JsonConfigFileUtil.getAndLoad(jsonName, modId, serializer, deserializer);
        } else {

            JsonConfigFileUtil.getAndLoad(jsonName, serializer, deserializer);
        }
    }

    /**
     * @param ctx execution context
     * @param jsonName name of file
     * @param modId mod this command belongs to, will be used as main command name
     * @param translationKey translation key for success message
     */
    private static void sendFeedback(CommandContext<CommandSource> ctx, String jsonName, @Nullable String modId, String translationKey) {

        ctx.getSource().sendSuccess(getFeedbackComponent(jsonName, modId, translationKey), true);
    }

    /**
     * @param jsonName name of file
     * @param modId mod this command belongs to, will be used as main command name
     * @param translationKey translation key for success message
     * @return success message
     */
    private static IFormattableTextComponent getFeedbackComponent(String jsonName, @Nullable String modId, String translationKey) {

        return new TranslationTextComponent(translationKey, getClickableComponent(jsonName, modId));
    }

    /**
     * @param jsonName name of file
     * @param modId mod this command belongs to, will be used as main command name
     * @return component for opening config file directory in file browser
     */
    private static IFormattableTextComponent getClickableComponent(String jsonName, @Nullable String modId) {

        File filePath = modId != null ? JsonConfigFileUtil.getPathInDir(jsonName, modId) : JsonConfigFileUtil.getPath(jsonName);

        return new StringTextComponent(jsonName).withStyle(TextFormatting.UNDERLINE)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath.getAbsolutePath())));
    }

}