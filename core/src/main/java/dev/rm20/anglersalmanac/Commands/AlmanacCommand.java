package dev.rm20.anglersalmanac.Commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.permissions.provider.HytalePermissionsProvider;
import dev.rm20.anglersalmanac.Registration.CommandInfo;

import javax.annotation.Nonnull;

@CommandInfo(
        name = "almanac",
        description = "Angler's Almanac commands",
        aliases = {"aa", "angler", "anglersalmanac"}
)
public class AlmanacCommand extends AbstractCommandCollection {

    public AlmanacCommand(@Nonnull String name, @Nonnull String description) {
        super(name, description);
        requireNoPermission();
    }

    public AlmanacCommand() {
        super("almanac", "Angler's Almanac commands");
        requireNoPermission();
    }
}
