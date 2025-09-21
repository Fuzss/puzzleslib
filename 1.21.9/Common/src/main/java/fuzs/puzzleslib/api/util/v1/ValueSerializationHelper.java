package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Consumer;

/**
 * A helper for working with {@link ValueOutput} and {@link ValueInput}.
 */
public final class ValueSerializationHelper {

    private ValueSerializationHelper() {
        // NO-OP
    }

    /**
     * Writes data using a {@link ValueOutput} to a {@link CompoundTag}.
     *
     * @param pathElement         the problem reporter path
     * @param valueOutputConsumer the value output consumer
     * @return the compound tag
     */
    public static CompoundTag save(ProblemReporter.PathElement pathElement, Consumer<ValueOutput> valueOutputConsumer) {
        return save(pathElement, RegistryAccess.EMPTY, valueOutputConsumer);
    }

    /**
     * Writes data using a {@link ValueOutput} to a {@link CompoundTag}.
     *
     * @param pathElement         the problem reporter path
     * @param registries          the dynamic registries
     * @param valueOutputConsumer the value output consumer
     * @return the compound tag
     */
    public static CompoundTag save(ProblemReporter.PathElement pathElement, HolderLookup.Provider registries, Consumer<ValueOutput> valueOutputConsumer) {
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(pathElement,
                PuzzlesLib.LOGGER)) {
            TagValueOutput valueOutput;
            if (registries != RegistryAccess.EMPTY) {
                valueOutput = TagValueOutput.createWithContext(scopedCollector, registries);
            } else {
                valueOutput = TagValueOutput.createWithoutContext(scopedCollector);
            }
            valueOutputConsumer.accept(valueOutput);
            return valueOutput.buildResult();
        }
    }

    /**
     * Reads data using a {@link ValueInput} from a {@link CompoundTag}.
     *
     * @param pathElement        the problem reporter path
     * @param registries         the dynamic registries
     * @param compoundTag        the compound tag
     * @param valueInputConsumer the value input consumer
     */
    public static void load(ProblemReporter.PathElement pathElement, HolderLookup.Provider registries, CompoundTag compoundTag, Consumer<ValueInput> valueInputConsumer) {
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(pathElement,
                PuzzlesLib.LOGGER)) {
            ValueInput valueInput = TagValueInput.create(scopedCollector, registries, compoundTag);
            valueInputConsumer.accept(valueInput);
        }
    }
}
