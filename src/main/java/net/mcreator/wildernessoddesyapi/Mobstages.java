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

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Mobstages {
    private static int daysElapsed = 0;
    private static final int BASE_MOB_SPAWN_RATE = 5; // Base rate of mob spawning

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
    public static void onMobSpawn(MobSpawnEvent.CheckSpawn event) {
        if (event.getEntity().getType().getCategory() == MobCategory.MONSTER) {
            Level world = (Level) event.getWorld();
            if (world.dimension() == Level.OVERWORLD) {
                int additionalMobs = daysElapsed / BASE_MOB_SPAWN_RATE;
                int maxMobs = ModConfig.COMMON.maxMobs.get();
                if (world.random.nextInt(100) < additionalMobs && additionalMobs < maxMobs) {
                    event.setResult(LivingSpawnEvent.Result.ALLOW);
                }
            }
        }
    }
}