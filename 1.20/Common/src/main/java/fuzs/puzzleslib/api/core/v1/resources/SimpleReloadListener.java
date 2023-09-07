/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fuzs.puzzleslib.api.core.v1.resources;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A simplified version of the "resource reload listener" interface, hiding the
 * peculiarities of the API.
 *
 * <p>In essence, there are two stages:
 *
 * <ul><li>load: create an instance of your data object containing all loaded and
 * processed information,
 * <li>apply: apply the information from the data object to the game instance.</ul>
 *
 * <p>The load stage should be self-contained as it can run on any thread! However,
 * the apply stage is guaranteed to run on the game thread.
 *
 * @param <T> The data object.
 */
public interface SimpleReloadListener<T> extends PreparableReloadListener {

    @Override
    default CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return this.load(resourceManager, preparationsProfiler, backgroundExecutor).thenCompose(preparationBarrier::wait).thenCompose(data -> {
            return this.apply(data, resourceManager, reloadProfiler, gameExecutor);
        });
    }

    /**
     * Asynchronously process and load resource-based data. The code
     * must be thread-safe and not modify game state!
     *
     * @param resourceManager The resource manager used during reloading.
     * @param profiler        The profiler which may be used for this stage.
     * @param executor        The executor which should be used for this stage.
     * @return A CompletableFuture representing the "data loading" stage.
     */
    CompletableFuture<T> load(ResourceManager resourceManager, ProfilerFiller profiler, Executor executor);

    /**
     * Synchronously apply loaded data to the game state.
     *
     * @param resourceManager The resource manager used during reloading.
     * @param profiler        The profiler which may be used for this stage.
     * @param executor        The executor which should be used for this stage.
     * @return A CompletableFuture representing the "data applying" stage.
     */
    CompletableFuture<Void> apply(T data, ResourceManager resourceManager, ProfilerFiller profiler, Executor executor);
}
