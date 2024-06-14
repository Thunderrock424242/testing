package net.mcreator.wildernessoddesyapi.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.wildernessoddesyapi.network.WildernessOddesyApiModVariables;

public class SettingUpCrawlProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (!(entity.getCapability(WildernessOddesyApiModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new WildernessOddesyApiModVariables.PlayerVariables())).is_player_crawling) {
			{
				boolean _setval = true;
				entity.getCapability(WildernessOddesyApiModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
					capability.is_player_crawling = _setval;
					capability.syncPlayerVariables(entity);
				});
			}
		}
		assert Boolean.TRUE; //#dbg:SettingUpCrawl:marker1
	}
}
