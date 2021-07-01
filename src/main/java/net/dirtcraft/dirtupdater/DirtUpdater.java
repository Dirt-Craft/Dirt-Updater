package net.dirtcraft.dirtupdater;

import com.google.inject.Inject;
import net.dirtcraft.dirtupdater.Configuration.ConfigManager;
import net.dirtcraft.dirtupdater.OreRepository.List;
import net.dirtcraft.dirtupdater.Utils.DataUtils;
import net.dirtcraft.dirtupdater.Utils.RegisterCommands;
import net.dirtcraft.dirtupdater.Utils.Updater;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

@Plugin(
        id = "dirt-updater",
        name = "Dirt Updater",
        description = "DirtCraft's plugin updater plugin (A.K.A. PUP)",
        authors = {
                "juliann",
                "ExperiencedDev/MrDj200"
        }
)
public class DirtUpdater {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    private ConfigManager configManager;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    private static DirtUpdater instance;

    @Listener
    public void onPreInit(GameConstructionEvent event) {
        instance = this;
        configManager = new ConfigManager(loader);
        Updater updater = new Updater();
        updater.checkUpdates();
        DataUtils.logPlugins();
        Task.builder()
                .async()
                .execute(updater::checkAndRebootIfNeeded)
                .submit(DirtUpdater.getInstance());
        List.listPlugins();
    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        new RegisterCommands();
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event){
        new Updater().checkUpdates();
    }

    public static Logger getLogger() {
        return instance.logger;
    }

    public static PluginContainer getContainer() {
        return instance.container;
    }

    public static DirtUpdater getInstance() {
        return instance;
    }
}
