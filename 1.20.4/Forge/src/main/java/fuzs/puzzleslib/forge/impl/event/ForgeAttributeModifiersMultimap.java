package fuzs.puzzleslib.forge.impl.event;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ForgeAttributeModifiersMultimap extends ForwardingMultimap<Attribute, AttributeModifier> {
    private final Supplier<Multimap<Attribute, AttributeModifier>> getModifiers;
    private final BiPredicate<Attribute, AttributeModifier> addModifier;
    private final BiPredicate<Attribute, AttributeModifier> removeModifier;
    private final Function<Attribute, Collection<AttributeModifier>> removeAttribute;
    private final Runnable clearModifiers;

    public ForgeAttributeModifiersMultimap(Supplier<Multimap<Attribute, AttributeModifier>> getModifiers, BiPredicate<Attribute, AttributeModifier> addModifier, BiPredicate<Attribute, AttributeModifier> removeModifier, Function<Attribute, Collection<AttributeModifier>> removeAttribute, Runnable clearModifiers) {
        this.getModifiers = getModifiers;
        this.addModifier = addModifier;
        this.removeModifier = removeModifier;
        this.removeAttribute = removeAttribute;
        this.clearModifiers = clearModifiers;
    }

    @Override
    protected Multimap<Attribute, AttributeModifier> delegate() {
        return this.getModifiers.get();
    }

    @Override
    public void clear() {
        this.clearModifiers.run();
    }

    @Override
    public boolean put(Attribute key, AttributeModifier value) {
        return this.addModifier.test(key, value);
    }

    @Override
    public boolean putAll(Attribute key, Iterable<? extends AttributeModifier> values) {
        boolean changed = false;
        for (AttributeModifier value : values) {
            changed |= this.put(key, value);
        }
        return changed;
    }

    @Override
    public boolean putAll(Multimap<? extends Attribute, ? extends AttributeModifier> multimap) {
        boolean changed = false;
        for (Map.Entry<? extends Attribute, ? extends AttributeModifier> entry : multimap.entries()) {
            changed |= this.put(entry.getKey(), entry.getValue());
        }
        return changed;
    }

    @Override
    public boolean remove(Object key, Object value) {
        return key instanceof Attribute attribute && value instanceof AttributeModifier attributeModifier && this.removeModifier.test(attribute, attributeModifier);
    }

    @Override
    public Collection<AttributeModifier> removeAll(Object key) {
        return key instanceof Attribute attribute ? this.removeAttribute.apply(attribute) : Collections.emptyList();
    }

    @Override
    public Collection<AttributeModifier> replaceValues(Attribute key, Iterable<? extends AttributeModifier> values) {
        Collection<AttributeModifier> collection = this.removeAll(key);
        this.putAll(key, values);
        return collection;
    }
}
