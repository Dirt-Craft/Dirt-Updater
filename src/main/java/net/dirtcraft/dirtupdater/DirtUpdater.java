package net.dirtcraft.dirtupdater;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import net.dirtcraft.dirtupdater.Configuration.ConfigManager;
import net.dirtcraft.dirtupdater.Configuration.PluginConfiguration;
import net.dirtcraft.dirtupdater.Utils.DataUtils;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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

    public static String globalJSONString = DataUtils.getStringFromURL("http://164.132.201.67/plugin/update.json");
    public static JsonObject globalJSON = DataUtils.getJsonObjFromString(DataUtils.getStringFromURL("http://164.132.201.67/plugin/update.json"));

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;
        configManager = new ConfigManager(loader);
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        checkUpdates();
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event){
        logger.info("Checking for updates...");
    }

    private void checkUpdates() {
        final File pluginDir = DirtUpdater.getContainer().getSource().get().getParent().toFile();

        DataUtils.jsonToMap().forEach((name, url) -> {

            int buildNumber = DataUtils.getBuildNumber(DataUtils.getJsonObjFromString(DataUtils.getStringFromURL(url)));

            if (!PluginConfiguration.Main.Projects.containsKey(name)) {
                PluginConfiguration.Main.Projects.put(name, buildNumber);
                ConfigManager.save();
                logger.warn("Added new plugin: " + name);
            } else if (PluginConfiguration.Main.Projects.get(name) != buildNumber) {
                for (File plugins : pluginDir.listFiles()) {
                    if (plugins.getName().toLowerCase().contains(name.toLowerCase())) {
                        plugins.delete();

                        JsonObject js = DataUtils.getJsonObjFromString(DataUtils.getStringFromURL(globalJSON.get(name).getAsString()));

                        String pluginName = DataUtils.getFileName(js);

                        try {
                            FileUtils.copyURLToFile(new URL(
                                    DataUtils.getArtifactJarFromJson(js)
                                    .replace("\"", "")
                                    .replace("jenkins.dirtcraft.net", "164.132.201.67:8080")
                            ), new File(pluginDir + File.separator + pluginName));

                            PluginConfiguration.Main.Projects.put(name, buildNumber);
                            ConfigManager.save();

                            logger.warn(pluginName + " " + "has been downloaded successfully!");

                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static DirtUpdater getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return getInstance().logger;
    }

    public static PluginContainer getContainer() {
        return getInstance().container;
    }
}
