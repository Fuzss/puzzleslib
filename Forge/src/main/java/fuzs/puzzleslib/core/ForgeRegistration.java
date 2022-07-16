package fuzs.puzzleslib.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Forge implementation of {@link CommonRegistration}
 */
public class ForgeRegistration implements CommonRegistration, ContainsModEvents {
    /**
     * all the mod event buses this instance has been registered to,
     * it is important to not register more than once as the events will also run every time, resulting in duplicate content
     */
    private final Set<IEventBus> modEventBuses = Collections.synchronizedSet(Sets.newIdentityHashSet());
    private final Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> newEntityAttributes = Maps.newConcurrentMap();

    @Override
    public <T extends Mob> void registerSpawnPlacement(EntityType<T> entityType, SpawnPlacements.Type location, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
        SpawnPlacements.register(entityType, location, heightmap, spawnPredicate);
    }

    @Override
    public void registerEntityAttribute(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        this.registerModEventBus();
        // we don't need to builder on Forge, but just keep it until registration
        this.newEntityAttributes.put(type, builder);
    }

    @Override
    public void modifyEntityAttribute(EntityType<? extends LivingEntity> type, Attribute attribute, double attributeValue) {

    }

    @SubscribeEvent
    public void onEntityAttributeCreation(final EntityAttributeCreationEvent evt) {
        this.newEntityAttributes.forEach((type, builder) -> evt.put(type, builder.build()));
    }

    @Override
    public Set<IEventBus> getModEventBuses() {
        return this.modEventBuses;
    }
}
