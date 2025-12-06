package fuzs.puzzleslib.api.client.renderer.v1.model.geom.builders;

import fuzs.puzzleslib.api.client.renderer.v1.model.geom.ModelPart;
import fuzs.puzzleslib.api.client.renderer.v1.model.geom.PartPose;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Copied from Minecraft 1.21.10.
 */
public class PartDefinition extends net.minecraft.client.model.geom.builders.PartDefinition {

    public PartDefinition(List<CubeDefinition> cubes, PartPose partPose) {
        super(cubes, partPose);
    }

    public PartDefinition(net.minecraft.client.model.geom.builders.PartDefinition partDefinition) {
        super(partDefinition.cubes, partDefinition.partPose);
        this.children.putAll(partDefinition.children);
    }

    @Override
    public PartDefinition addOrReplaceChild(String name, CubeListBuilder cubes, net.minecraft.client.model.geom.PartPose partPose) {
        PartDefinition partDefinition = new PartDefinition(cubes.getCubes(),
                partPose instanceof PartPose ? (PartPose) partPose : new PartPose(partPose));
        return this.addOrReplaceChild(name, partDefinition);
    }

    public PartDefinition addOrReplaceChild(String name, PartDefinition child) {
        PartDefinition partDefinition = (PartDefinition) this.children.put(name, child);
        if (partDefinition != null) {
            child.children.putAll(partDefinition.children);
        }

        return child;
    }

    public PartDefinition clearRecursively() {
        for (String string : this.children.keySet()) {
            this.clearChild(string).clearRecursively();
        }

        return this;
    }

    public PartDefinition clearChild(String name) {
        PartDefinition partDefinition = (PartDefinition) this.children.get(name);
        if (partDefinition == null) {
            throw new IllegalArgumentException("No child with name: " + name);
        } else {
            return this.addOrReplaceChild(name, CubeListBuilder.create(), partDefinition.partPose);
        }
    }

    public void retainPartsAndChildren(Set<String> parts) {
        for (Entry<String, net.minecraft.client.model.geom.builders.PartDefinition> entry : this.children.entrySet()) {
            PartDefinition partDefinition = (PartDefinition) entry.getValue();
            if (!parts.contains(entry.getKey())) {
                this.addOrReplaceChild(entry.getKey(), CubeListBuilder.create(), partDefinition.partPose)
                        .retainPartsAndChildren(parts);
            }
        }
    }

    public void retainExactParts(Set<String> parts) {
        for (Entry<String, net.minecraft.client.model.geom.builders.PartDefinition> entry : this.children.entrySet()) {
            PartDefinition partDefinition = (PartDefinition) entry.getValue();
            if (parts.contains(entry.getKey())) {
                partDefinition.clearRecursively();
            } else {
                this.addOrReplaceChild(entry.getKey(), CubeListBuilder.create(), partDefinition.partPose)
                        .retainExactParts(parts);
            }
        }
    }

    @Override
    public ModelPart bake(int texWidth, int texHeight) {
        Object2ObjectArrayMap<String, ModelPart> object2ObjectArrayMap = this.getChildren()
                .stream()
                .collect(
                        Collectors.toMap(
                                Entry::getKey,
                                (Entry<String, PartDefinition> entry) -> {
                                    return entry.getValue().bake(texWidth, texHeight);
                                },
                                (ModelPart oldModelPart, ModelPart modelPartx) -> {
                                    return oldModelPart;
                                },
                                Object2ObjectArrayMap::new
                        )
                );
        List<ModelPart.Cube> list = this.cubes.stream().map((CubeDefinition cube) -> {
            return cube.bake(texWidth, texHeight);
        }).toList();
        ModelPart modelPart = new ModelPart(list, object2ObjectArrayMap);
        modelPart.setInitialPose(this.partPose);
        modelPart.loadPose(this.partPose);
        return modelPart;
    }

    @Override
    public PartDefinition getChild(String name) {
        return (PartDefinition) this.children.get(name);
    }

    public Set<Entry<String, PartDefinition>> getChildren() {
        return (Set<Entry<String, PartDefinition>>) (Set<?>) this.children.entrySet();
    }

    public PartDefinition transformed(UnaryOperator<PartPose> transformer) {
        PartDefinition partDefinition = new PartDefinition(this.cubes, transformer.apply((PartPose) this.partPose));
        partDefinition.children.putAll(this.children);
        return partDefinition;
    }
}
