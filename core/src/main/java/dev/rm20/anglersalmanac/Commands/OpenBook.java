package dev.rm20.anglersalmanac.Commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.rm20.anglersalmanac.AnglersAlmanac;
import dev.rm20.anglersalmanac.Inventory.FishBagUIHandler;
import dev.rm20.anglersalmanac.Registration.CommandInfo;
import org.jspecify.annotations.NonNull;

import static dev.rm20.anglersalmanac.AlmanacBook.BookPageManager.OpenPage;

@CommandInfo(
        name = "stats",
        description = "Open your fishing book to display fish",
        aliases = {"openbook", "book", "info"},
        parent = "almanac"
)
public class OpenBook extends AbstractPlayerCommand {

    public OpenBook(@NonNull String name, @NonNull String description) {
        super(name, description);
        requireNoPermission();
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        UUIDComponent uuid = store.getComponent(ref, UUIDComponent.getComponentType());
        if(uuid == null)
        {
            commandContext.sendMessage(Message.parse("Error while opening book"));
            return;
        }
        if (player != null) {
            //AnglersAlmanac.LOGGER.atInfo().log(playerRef.getUsername());
            OpenPage(player,0, uuid.toString(),playerRef.getUsername());
        } else {
            commandContext.sendMessage(Message.translation("anglersalmanac.cmd.error.notPlayer"));
        }
    }
}
