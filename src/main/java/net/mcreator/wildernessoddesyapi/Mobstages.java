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

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

@Mod("wilderness_oddesy_api")
public class NightTimeMobsMod {
    public static final String MODID = "wilderness_oddesy_api";
    private static int daysElapsed = 0;
    private static final int BASE_MOB_SPAWN_RATE = 5; // Base rate of mob spawning

    public NightTimeMobsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigHolder.COMMON_SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Initialization code
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        Level world = event.world;
        if (world.dimension() == Level.OVERWORLD && !world.isClientSide) {
            if (world.getDayTime() % 24000 == 0) { // Check if it's a new day
                daysElapsed++;
            }
        }
    }

    @SubscribeEvent
    public static void onMobSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity().getType().getCategory() == MobCategory.MONSTER) {
            Level world = (Level) event.getWorld();
            if (world.dimension() == Level.OVERWORLD) {
                if (daysElapsed <= 20) {
                    int additionalMobs = daysElapsed / BASE_MOB_SPAWN_RATE;
                    int maxMobs = ModConfigHolder.COMMON.maxMobs.get();
                    if (world.random.nextInt(100) < additionalMobs && additionalMobs < maxMobs) {
                        event.setResult(LivingSpawnEvent.Result.ALLOW);
                    }
                } else {
                    // Revert to Minecraft's normal spawning system after 20 days
                    event.setResult(LivingSpawnEvent.Result.DEFAULT);
                }
            }
        }
    }

    public static class ModConfigHolder {
        public static final Common COMMON;
        public static final ForgeConfigSpec COMMON_SPEC;

        static {
            Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
            COMMON = commonSpecPair.getLeft();
            COMMON_SPEC = commonSpecPair.getRight();
        }

        public static class Common {
            public final ForgeConfigSpec.IntValue maxMobs;

            public Common(ForgeConfigSpec.Builder builder) {
                builder.push("general");
                maxMobs = builder
                        .comment("Maximum number of mobs that can spawn")
                        .defineInRange("maxMobs", 100, 1, 1000);
                builder.pop();
            }
        }
    }
}
