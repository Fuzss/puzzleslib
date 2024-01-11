package fuzs.puzzleslib.api.biome.v1;

/**
 * To achieve a predictable order for biome modifiers, and to aid with mod compatibility, modifiers need to declare
 * the phase in which they will be applied.
 *
 * <p>This will result in the following order:
 * <ol>
 *     <li>Additions to biomes</li>
 *     <li>Removals from biomes</li>
 *     <li>Replacements (removal + add) in biomes</li>
 *     <li>Generic post-processing of biomes</li>
 * </ol>
 *
 * <p>Mostly copied from Fabric API's Biome API, specifically <code>net.fabricmc.fabric.api.biome.v1.ModificationPhase</code>
 * to allow for use in common project and to allow reimplementation on Forge using Forge's native biome modification system.
 *
 * <p>Copyright (c) FabricMC
 * <p>SPDX-License-Identifier: Apache-2.0
 */
public enum BiomeLoadingPhase {
    /**
     * The appropriate phase for enriching biomes by adding to them without relying on
     * other information in the biome, or removing other features.
     *
     * <p><b>Examples:</b> New ores, new vegetation, new structures
     */
    ADDITIONS,

    /**
     * The appropriate phase for modifiers that remove features or other aspects of biomes (i.e. removal of spawns,
     * removal of features, etc.).
     *
     * <p><b>Examples:</b> Remove iron ore from plains, remove ghasts
     */
    REMOVALS,

    /**
     * The appropriate phase for modifiers that replace existing features with modified features.
     *
     * <p><b>Examples:</b> Replace mineshafts with biome-specific mineshafts
     */
    MODIFICATIONS,

    /**
     * The appropriate phase for modifiers that perform wide-reaching biome postprocessing.
     *
     * <p><b>Examples:</b> Mods that allow modpack authors to customize world generation, changing biome
     * properties (i.e. category) that other mods rely on.
     */
    POST_PROCESSING
}
