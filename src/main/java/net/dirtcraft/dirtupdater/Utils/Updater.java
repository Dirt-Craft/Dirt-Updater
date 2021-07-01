package net.dirtcraft.dirtupdater.Utils;

import com.google.gson.JsonObject;
import net.dirtcraft.dirtupdater.Configuration.ConfigManager;
import net.dirtcraft.dirtupdater.Configuration.PluginConfiguration;
import net.dirtcraft.dirtupdater.DirtUpdater;
import org.apache.commons.io.FileUtils;
import org.spongepowered.common.mixin.handler.TerminateVM;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class Updater {

    public static final JsonObject globalJSON = DataUtils.getJsonObjFromString(DataUtils.getStringFromURL("http://164.132.201.67/plugin/update.json"));
    private boolean didUpdate = false;

    public void checkUpdates() {
        DirtUpdater.getLogger().info("Checking for updates...");

        if (!DirtUpdater.getContainer().getSource().isPresent()) {
            DirtUpdater.getLogger().error("Could not retrieve plugins directory!");
            return;
        }
        final File pluginDir = DirtUpdater.getContainer()
                .getSource().get()
                .getParent()
                .toFile();
        final File modDir = "plugins".equals(pluginDir.getName().toLowerCase(Locale.ROOT))? pluginDir.getParentFile() : pluginDir;

        DataUtils.jsonToMap().forEach((name, url)-> updateArtifact(name, url, pluginDir));
        PluginConfiguration.Main.Mods.forEach((name, url)-> updateArtifact(name, url, modDir));
        ConfigManager.save();
    }

    public void checkAndRebootIfNeeded(){
        checkUpdates();
        if (didUpdate) TerminateVM.terminate("net.minecraftforge.fml", 1);
    }

    private void updateArtifact(String name, String url, File folder) {
        if (!PluginConfiguration.Main.Update.computeIfAbsent(name, x -> true)) return;
        int buildNumber = DataUtils.getBuildNumber(DataUtils.getJsonObjFromString(DataUtils.getStringFromURL(url)));

        if (!PluginConfiguration.Main.Projects.containsKey(name)) {
            try {
                DirtUpdater.getLogger().warn("Added new plugin: " + name);
                PluginConfiguration.Main.Projects.put(name, buildNumber);
                ConfigManager.save();
                downloadArtifact(folder, name, buildNumber);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

        } else if (!checkPluginDir(folder, name)) {
            try {
                downloadArtifact(folder, name, buildNumber);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else if (PluginConfiguration.Main.Projects.get(name) != buildNumber) {
            for (File plugins : folder.listFiles()) {
                if (plugins.getName().toLowerCase().contains(name.toLowerCase())) {
                    try {
                        plugins.delete();
                        downloadArtifact(folder, name, buildNumber);
                        didUpdate = true;
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    private void downloadArtifact(File pluginDir, String name, int buildNumber) throws IOException {
        JsonObject js = DataUtils.getJsonObjFromString(DataUtils.getStringFromURL(globalJSON.get(name).getAsString()));
        String pluginName = DataUtils.getFileName(js);

        FileUtils.copyURLToFile(new URL(
                DataUtils.getArtifactJarFromJson(js)
                        .replace("\"", "")
                        .replace("jenkins.dirtcraft.net", "164.132.201.67:8080")
        ), new File(pluginDir + File.separator + pluginName));

        PluginConfiguration.Main.Projects.put(name, buildNumber);
        ConfigManager.save();

        DataUtils.logUpdates(name);

        DirtUpdater.getLogger().warn(pluginName + " " + "has been downloaded successfully!");
    }

    private boolean checkPluginDir(File pluginDir, String name) {
        for (File plugins : pluginDir.listFiles()) {
            if (plugins.getName().toLowerCase().contains(name.toLowerCase())) return true;
        }
        DirtUpdater.getLogger().warn("Could not find \"" + name + "\" jar, downloading a new one...");
        return false;
    }
}
