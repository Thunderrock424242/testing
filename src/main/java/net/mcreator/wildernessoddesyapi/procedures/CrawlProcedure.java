package net.mcreator.wildernessoddesyapi.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity;

import net.mcreator.wildernessoddesyapi.network.WildernessOddesyApiModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class CrawlProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player);
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if ((entity.getCapability(WildernessOddesyApiModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new WildernessOddesyApiModVariables.PlayerVariables())).is_player_crawling == true) {
			entity.setPose(Pose.SWIMMING);
		}
		assert Boolean.TRUE; //#dbg:Crawl:marker1
	}
}
