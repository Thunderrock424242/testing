/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.mcreator.wildernessoddesyapi as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.mcreator.wildernessoddesyapi;

import net.minecraft.resources.ResourceLocation; 
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;

@Mod("wilderness_oddesy_api")
public class MemoryOptimizer {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ForgeConfigSpec CONFIG_SPEC;
    private static final ForgeConfigSpec.BooleanValue ENABLE_OPTIMIZATION;
    private static final ForgeConfigSpec.IntValue OPTIMIZATION_INTERVAL;

    static {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        CONFIG_SPEC = specPair.getRight();
        ENABLE_OPTIMIZATION = specPair.getLeft().enableOptimization;
        OPTIMIZATION_INTERVAL = specPair.getLeft().optimizationInterval;
    }

    private int tickCount = 0;

    public MemoryOptimizer() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (event.phase != TickEvent.Phase.END || !ENABLE_OPTIMIZATION.get()) return;

        if (++tickCount >= OPTIMIZATION_INTERVAL.get()) {
            tickCount = 0;
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientOptimization::optimize);
            DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> ServerOptimization::optimize);
        }
    }

    private static void logMemoryUsage(String phase) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = allocatedMemory - freeMemory;
        long availableMemory = maxMemory - usedMemory;

        LOGGER.debug("{}:", phase);
        LOGGER.debug("Max Memory: {} MB", maxMemory / 1024 / 1024);
        LOGGER.debug("Allocated Memory: {} MB", allocatedMemory / 1024 / 1024);
        LOGGER.debug("Free Memory: {} MB", freeMemory / 1024 / 1024);
        LOGGER.debug("Used Memory: {} MB", usedMemory / 1024 / 1024);
        LOGGER.debug("Available Memory: {} MB", availableMemory / 1024 / 1024);
    }

    private static class Config {
        final ForgeConfigSpec.BooleanValue enableOptimization;
        final ForgeConfigSpec.IntValue optimizationInterval;

        Config(ForgeConfigSpec.Builder builder) {
            enableOptimization = builder.comment("Enable memory optimization features").define("enableOptimization", true);
            optimizationInterval = builder.comment("Ticks between each optimization attempt").defineInRange("optimizationInterval", 6000, 1, Integer.MAX_VALUE);
        }
    }

    private static class ClientOptimization {
        private static final Map<ResourceLocation, SoftReference<ModelResource>> loadedModels = new HashMap<>();
        private static final Set<ResourceLocation> currentlyUsedModels = new HashSet<>();
        private static final Queue<ReusableObject> objectPool = new LinkedList<>();
        // Add a simple cache for frequently used data
        private static final Map<String, Object> dataCache = new HashMap<>();

        private static void optimize() {
            logMemoryUsage("Client Optimization - Memory Usage Details");
            unloadUnusedModels();
            // Consider cleaning up the cache if necessary
        }

        private static void unloadUnusedModels() {
            Iterator<Map.Entry<ResourceLocation, SoftReference<ModelResource>>> it = loadedModels.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<ResourceLocation, SoftReference<ModelResource>> entry = it.next();
                ResourceLocation modelId = entry.getKey();
                SoftReference<ModelResource> modelRef = entry.getValue();

                if (!currentlyUsedModels.contains(modelId) && modelRef.get() == null) {
                    it.remove();
                    System.out.println("Unloaded unused model: " + modelId);
                }
            }
            currentlyUsedModels.clear();
        }

        public static void useModel(ResourceLocation modelId) {
            currentlyUsedModels.add(modelId);
            loadedModels.computeIfAbsent(modelId, ClientOptimization::loadModel);
        }

        private static SoftReference<ModelResource> loadModel(ResourceLocation modelId) {
            System.out.println("Loading model: " + modelId);
            ReusableObject model = getObjectFromPool();
            return new SoftReference<>(new ModelResource(model));
        }

        private static ReusableObject getObjectFromPool() {
            ReusableObject obj = objectPool.poll();
            if (obj == null) {
                obj = new ReusableObject();
            }
            return obj;
        }

        public static void returnObjectToPool(ReusableObject object) {
            object.reset();
            objectPool.offer(object);
        }

        private static class ModelResource {
            private ReusableObject reusablePart;

            public ModelResource(ReusableObject reusablePart) {
                this.reusablePart = reusablePart;
            }
        }

        private static class ReusableObject {
            public void reset() {
            }
        }

        // Example method for caching data
        public static void cacheData(String key, Object data) {
            dataCache.put(key, data);
        }

        // Example method for retrieving cached data
        public static Object getCachedData(String key) {
            return dataCache.get(key);
        }
    }

    private static class ServerOptimization {
        private static void optimize() {
            logMemoryUsage("Server Optimization - Memory Usage Details");
        }
    }
}
