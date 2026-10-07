package dev.rm20.anglersalmanac.Inventory;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerWindow;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import dev.rm20.anglersalmanac.AnglersAlmanac;

import javax.annotation.Nonnull;

public class FishBagWindow extends ContainerWindow {
    public FishBagWindow(@Nonnull ItemContainer itemContainer) {
        super(itemContainer);
//        for (String id : Item.getAssetMap().getAssetMap().keySet()) {
//            if (id.toLowerCase().contains("chest") || id.toLowerCase().contains("fish") || id.toLowerCase().contains("rod")) {
//                AnglersAlmanac.LOGGER.atInfo().log(+ id);
//            }
//        }
        getData().addProperty("blockItemId", "Fishing_Bag");
        AnglersAlmanac.LOGGER.atInfo().log(String.valueOf(getData()));
    }
}
