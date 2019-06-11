package net.dirtcraft.dirtupdater;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import net.dirtcraft.dirtupdater.Configuration.ConfigManager;
import net.dirtcraft.dirtupdater.Utils.DataUtils;
import net.dirtcraft.dirtupdater.Utils.EventListeners;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

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

    public static final String globalJSONString = DataUtils.getStringFromURL("http://164.132.201.67/plugin/update.json");
    public static final JsonObject globalJSON = DataUtils.getJsonObjFromString(DataUtils.getStringFromURL("http://164.132.201.67/plugin/update.json"));

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;
        configManager = new ConfigManager(loader);
        Sponge.getEventManager().registerListeners(instance, new EventListeners());
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
