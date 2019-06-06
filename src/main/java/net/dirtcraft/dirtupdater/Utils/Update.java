package net.dirtcraft.dirtupdater.Utils;

import com.google.gson.JsonObject;
import net.dirtcraft.dirtupdater.Configuration.ConfigManager;
import net.dirtcraft.dirtupdater.Configuration.PluginConfiguration;
import net.dirtcraft.dirtupdater.DirtUpdater;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Update {

    public static void checkUpdates() {
        DirtUpdater.getLogger().info("Checking for updates...");

        if (!DirtUpdater.getContainer().getSource().isPresent()) {
            DirtUpdater.getLogger().error("Could not retrieve plugins directory!");
            return;
        }
        final File pluginDir = DirtUpdater.getContainer().getSource().get().getParent().toFile();

        DataUtils.jsonToMap().forEach((name, url) -> {

            int buildNumber = DataUtils.getBuildNumber(DataUtils.getJsonObjFromString(DataUtils.getStringFromURL(url)));

            if (!PluginConfiguration.Main.Projects.containsKey(name)) {
                try {
                    download(pluginDir, name, buildNumber);
                    PluginConfiguration.Main.Projects.put(name, buildNumber);
                    ConfigManager.save();
                    DirtUpdater.getLogger().warn("Added new plugin: " + name);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            } else if (!checkPluginDir(pluginDir, name)) {
                try {
                    download(pluginDir, name, buildNumber);
                    return;
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else if (PluginConfiguration.Main.Projects.get(name) != buildNumber) {
                    for (File plugins : pluginDir.listFiles()) {
                        if (plugins.getName().toLowerCase().contains(name.toLowerCase())) {
                            try {
                                plugins.delete();
                                download(pluginDir, name, buildNumber);
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
            }
        });
    }

    private static void download(File pluginDir, String name, int buildNumber) throws IOException {
        JsonObject js = DataUtils.getJsonObjFromString(DataUtils.getStringFromURL(DirtUpdater.globalJSON.get(name).getAsString()));
        String pluginName = DataUtils.getFileName(js);

        FileUtils.copyURLToFile(new URL(
                DataUtils.getArtifactJarFromJson(js)
                        .replace("\"", "")
                        .replace("jenkins.dirtcraft.net", "164.132.201.67:8080")
        ), new File(pluginDir + File.separator + pluginName));

        PluginConfiguration.Main.Projects.put(name, buildNumber);
        ConfigManager.save();

        DirtUpdater.getLogger().warn(pluginName + " " + "has been downloaded successfully!");
    }

    private static boolean checkPluginDir(File pluginDir, String name) {
        for (File plugins : pluginDir.listFiles()) {
            if (plugins.getName().toLowerCase().contains(name.toLowerCase())) return true;
        }
        DirtUpdater.getLogger().warn("Could not find \"" + name + "\" jar, downloading a new one...");
        return false;
    }
}
