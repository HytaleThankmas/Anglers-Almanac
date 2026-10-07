package dev.rm20.anglersalmanac.api;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import dev.rm20.anglersalmanac.Metadata.FishingContext;
import dev.rm20.anglersalmanac.Models.FishLoot;

import javax.annotation.Nullable;
import java.util.Collection;

public interface ILootProvider {
    FishLoot getFishData(String id);
    FishLoot getFishDataFromItemId(String itemId);
    Collection<? extends FishLoot> getAllLoot();

    FishLoot getRandomFish(FishingContext ctx, @Nullable Object modifiers);
}