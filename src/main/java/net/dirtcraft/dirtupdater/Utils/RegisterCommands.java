package net.dirtcraft.dirtupdater.Utils;

import net.dirtcraft.dirtupdater.Commands.ListOre;
import net.dirtcraft.dirtupdater.DirtUpdater;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class RegisterCommands {

    public RegisterCommands() {
        Sponge.getCommandManager().register(DirtUpdater.getInstance(), updates(), "updates");
    }

    private CommandSpec updates() {
        return CommandSpec.builder()
                .description(Text.of("List available updates from Sponge's Ore Repository"))
                .executor(new ListOre())
                .build();
    }

}
