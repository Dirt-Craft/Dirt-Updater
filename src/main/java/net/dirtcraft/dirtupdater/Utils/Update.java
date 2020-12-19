package net.dirtcraft.dirtupdater.Utils;

import com.google.gson.JsonObject;
import net.dirtcraft.dirtupdater.Configuration.ConfigManager;
import net.dirtcraft.dirtupdater.Configuration.PluginConfiguration;
import net.dirtcraft.dirtupdater.DirtUpdater;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class Update {

    public static boolean checkUpdates() {
        AtomicBoolean hasUpdated = new AtomicBoolean(false);
        DirtUpdater.getLogger().info("Checking for updates...");

        if (!DirtUpdater.getContainer().getSource().isPresent()) {
            DirtUpdater.getLogger().error("Could not retrieve plugins directory!");
            return hasUpdated.get();
        }
        final File pluginDir = DirtUpdater.getContainer().getSource().get().getParent().toFile();

        ConfigManager.load();
        DataUtils.jsonToMap().forEach((name, url) -> {
            Integer buildNumber = DataUtils.getBuildNumber(DataUtils.getJsonObjFromUrl(url));
            if (buildNumber == null) {
                DirtUpdater.getLogger().error("Could not retrieve build number from JSON. Is Jenkins offline?");
                return;
            }

            try {
                if (!PluginConfiguration.Main.Projects.containsKey(name)) {
                    for (File plugins : pluginDir.listFiles()) {
                        if (plugins.getName().toLowerCase().contains(name.toLowerCase().trim())) {
                            plugins.delete();
                            hasUpdated.set(true);
                        }
                    }
                    download(pluginDir, name, buildNumber);
                    DirtUpdater.getLogger().warn("Added new plugin: " + name);
                } else if (!checkPluginDir(pluginDir, name)) {
                    download(pluginDir, name, buildNumber);
                    hasUpdated.set(true);
                } else if (!PluginConfiguration.Main.Projects.get(name).equals(buildNumber)) {
                    for (File plugins : pluginDir.listFiles()) {
                        if (plugins.getName().toLowerCase().contains(name.toLowerCase().trim())) {
                            plugins.delete();
                            download(pluginDir, name, buildNumber);
                            hasUpdated.set(true);
                        }
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        return hasUpdated.get();
    }

    private static void download(File pluginDir, String name, int buildNumber) throws IOException {
        final JsonObject js = DataUtils.getJsonObjFromUrl(DirtUpdater.globalJSON.get(name).getAsString());
        String pluginName = DataUtils.getFileName(js);
        if (pluginName == null) throw new IOException("Could not get plugin name from JSON");

        FileUtils.copyURLToFile(new URL(
                DataUtils.getArtifactJarFromJson(js)
                        .replace("\"", "")
                        .replace("jenkins.dirtcraft.net", "164.132.201.67:8080")
        ), new File(pluginDir, pluginName));

        PluginConfiguration.Main.Projects.put(name, buildNumber);
        ConfigManager.save();

        DataUtils.logUpdates(name);

        DirtUpdater.getLogger().warn(pluginName + " " + "has been downloaded successfully!");
    }

    private static boolean checkPluginDir(File pluginDir, String name) {
        for (File plugins : pluginDir.listFiles()) {
            if (plugins.getName().toLowerCase().contains(name.toLowerCase().trim())) return true;
        }
        DirtUpdater.getLogger().warn("Could not find \"" + name + "\" jar, downloading a new one...");
        return false;
    }
}
