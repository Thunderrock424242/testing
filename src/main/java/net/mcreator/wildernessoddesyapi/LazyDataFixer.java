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

import com.google.common.base.Suppliers;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.LogicalSide;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = "wilderness_oddesy_api")
public class LazyDataFixer {

    private static final Logger LOGGER = LogManager.getLogger();
    // Wrap the BigObject creation in a more complex supplier if needed for explicit control
    private static final Supplier<BigObject> bigObjectSupplier = Suppliers.memoize(() -> {
        try {
            // Here you can include any explicit control logic, checks, or initializations
            return new BigObject();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize BigObject", e);
            throw new RuntimeException("BigObject initialization failed", e);
        }
    });

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT) {
            tryLoadBigObject("Client");
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            tryLoadBigObject("Server");
        }
    }

    private static void tryLoadBigObject(String side) {
        try {
            // Accessing the BigObject, including any initialization logic defined above
            BigObject bigObject = bigObjectSupplier.get();
            
        } catch (RuntimeException e) {
            LOGGER.error("Error loading BigObject on the {} side: {}", side, e.getMessage());
        }
    }

    static class BigObject {
        public BigObject() {
            // BigObject's initialization logic
            LOGGER.debug ("BigObject is being initialized...");
        }
    }
}

