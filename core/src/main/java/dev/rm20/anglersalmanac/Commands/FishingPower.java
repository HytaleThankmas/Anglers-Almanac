package dev.rm20.anglersalmanac.Commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.rm20.anglersalmanac.AnglersAlmanac;
import dev.rm20.anglersalmanac.Registration.CommandInfo;
import dev.rm20.anglersalmanac.Utils.FishingPowerUtils;
import org.jspecify.annotations.NonNull;

@CommandInfo(
        name = "power",
        description = "View your current fishing power, level, and luck",
        aliases = {"fishingpower", "luck"},
        parent = "almanac"
)
public class FishingPower extends AbstractPlayerCommand {

    public FishingPower(@NonNull String name, @NonNull String description) {
        super(name, description);
        requireNoPermission();
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        float totalPower = FishingPowerUtils.getTotalFishingPower(store, ref);
        int fishingLevel = (AnglersAlmanac.getInstance().skillTree != null)
                ? AnglersAlmanac.getInstance().skillTree.getFishingLevel(store, ref)
                : 1;
        double fishingLuck = (AnglersAlmanac.getInstance().skillTree != null)
                ? AnglersAlmanac.getInstance().skillTree.getFishingLuck(store, ref)
                : 0.0;

        //AnglersAlmanac.LOGGER.atInfo().log("Fishing Power for " + playerRef.getUuid() + ": " + totalPower + " | Luck: " + fishingLuck + " | Level: " + fishingLevel);

        commandContext.sendMessage(Message.raw("--- Fishing Stats ---"));
        commandContext.sendMessage(Message.raw("Fishing Power: " + String.format(java.util.Locale.ROOT, "%.2f", totalPower)));
        commandContext.sendMessage(Message.raw("Fishing Level: " + fishingLevel));
        commandContext.sendMessage(Message.raw("Fishing Luck: +" + String.format(java.util.Locale.ROOT, "%.1f%%", fishingLuck * 100.0)));
    }
}