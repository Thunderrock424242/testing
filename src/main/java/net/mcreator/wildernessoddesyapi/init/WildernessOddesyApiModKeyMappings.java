
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.wildernessoddesyapi.init;

import org.lwjgl.glfw.GLFW;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import net.mcreator.wildernessoddesyapi.network.CrawlKeyMessage;
import net.mcreator.wildernessoddesyapi.WildernessOddesyApiMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class WildernessOddesyApiModKeyMappings {
	public static final KeyMapping CRAWL_KEY = new KeyMapping("key.wilderness_oddesy_api.crawl_key", GLFW.GLFW_KEY_C, "key.categories.movement") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				WildernessOddesyApiMod.PACKET_HANDLER.sendToServer(new CrawlKeyMessage(0, 0));
				CrawlKeyMessage.pressAction(Minecraft.getInstance().player, 0, 0);
				CRAWL_KEY_LASTPRESS = System.currentTimeMillis();
			} else if (isDownOld != isDown && !isDown) {
				int dt = (int) (System.currentTimeMillis() - CRAWL_KEY_LASTPRESS);
				WildernessOddesyApiMod.PACKET_HANDLER.sendToServer(new CrawlKeyMessage(1, dt));
				CrawlKeyMessage.pressAction(Minecraft.getInstance().player, 1, dt);
			}
			isDownOld = isDown;
		}
	};
	private static long CRAWL_KEY_LASTPRESS = 0;

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(CRAWL_KEY);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				CRAWL_KEY.consumeClick();
			}
		}
	}
}
