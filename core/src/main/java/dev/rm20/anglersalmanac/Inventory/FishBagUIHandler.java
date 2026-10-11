package dev.rm20.anglersalmanac.Inventory;

import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.rm20.anglersalmanac.AnglersAlmanac;

public class FishBagUIHandler {



    public static void openFishBag(Player player) {
        var accessor = player.getReference().getStore();

        FishBagComponent.ensurePlayerHasFishBag(player);
        FishBagComponent fishBag = accessor.getComponent(
                player.getReference(),
                FishBagComponent.getComponentType()
        );

        if (fishBag == null) {
            return;
        }


        var container = fishBag.getInventory();
        player.getPageManager().setPageWithWindows(
                player.getReference(),
                player.getReference().getStore(),
                Page.Bench,
                true,
                new FishBagWindow(container)
        );



        AnglersAlmanac.LOGGER.atInfo().log("Opened bag");

    }
}
