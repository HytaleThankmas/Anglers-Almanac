package dev.rm20.anglersalmanac.Commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.rm20.anglersalmanac.AnglersAlmanac;
import dev.rm20.anglersalmanac.Inventory.FishBagUIHandler;
import dev.rm20.anglersalmanac.Inventory.FishMarket;
import dev.rm20.anglersalmanac.Registration.CommandInfo;
import org.jspecify.annotations.NonNull;

@CommandInfo(
        name = "sellallfish",
        description = "Open the fishing bag"
)
public class SellFish extends AbstractPlayerCommand {

    public SellFish(@NonNull String name, @NonNull String description) {
        super(name, description);
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            FishBagUIHandler.openFishMarket(player);
        }
    }
}
