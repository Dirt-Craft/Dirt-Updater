package net.dirtcraft.dirtupdater.Commands;

import net.dirtcraft.dirtupdater.DirtUpdater;
import net.dirtcraft.dirtupdater.OreRepository.List;
import net.dirtcraft.dirtupdater.Utils.DataUtils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.ArrayList;

public class ListOre implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {

        Task.builder()
                .async()
                .execute(() ->
                        PaginationList.builder()
                                .title(DataUtils.format("&cDirt&8-&7Updater"))
                                .padding(DataUtils.format("&4&m-"))
                                .contents(contents())
                                .build()
                                .sendTo(source))
                .submit(DirtUpdater.getInstance());

        return CommandResult.success();
    }

    private ArrayList<Text> contents() {
        ArrayList<Text> contents = new ArrayList<>();

        List.listPlugins().forEach((plugin, newVersion) -> {
            if (!plugin.getVersion().isPresent()) return;

            contents.add(Text.builder()
                    .append(DataUtils.format(("&8&l» &6" + plugin.getName()).replace("Â", "")))
                    .onHover(TextActions.showText(DataUtils.format("&6Ore &eRepository&r\n" +
                            "\n&7Current Version&8: &6" + plugin.getVersion().get() + "&r\n" +
                            "&7Recommended Version&8: &a" + newVersion)))
                    .build());

        });

        return contents;
    }
}
