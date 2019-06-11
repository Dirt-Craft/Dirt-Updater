package net.dirtcraft.dirtupdater.OreRepository;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.dirtcraft.dirtupdater.DirtUpdater;
import net.dirtcraft.dirtupdater.Utils.DataUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class List {

    public static final String API_URL = "https://ore.spongepowered.org/api/v1/projects/";

    @Deprecated
    public static HashMap<PluginContainer, String> listPlugins() {
        if (!Sponge.getPlatform().getApi().getVersion().isPresent()) {
            DirtUpdater.getLogger().error("Could not detect API version, skipping Ore Repository updates!");
            return null;
        }
        if (!Sponge.getPlatform().getApi().getVersion().get().startsWith("7")) {
            DirtUpdater.getLogger().error("Not running API 7 or above, skipping Ore Repository updates!");
            return null;
        }

        HashMap<PluginContainer, String> pluginMap = new HashMap<>();

        for (PluginContainer plugin : Sponge.getPluginManager().getPlugins()) {
            if (!plugin.getVersion().isPresent()) continue;
            try {
                JsonObject pluginJSON = DataUtils.getJsonObjFromString(DataUtils.getStringFromURL(API_URL + plugin.getId()));
                if (!pluginJSON.get("recommended").getAsJsonObject()
                        .get("dependencies").getAsJsonArray()
                        .get(0).getAsJsonObject()
                        .get("version")
                        .getAsString().startsWith("7")) {
                    DirtUpdater.getLogger().warn(plugin.getName() + " is not on API 7, skipping update check...");
                    continue;
                }
                if (plugin.getVersion().get().equalsIgnoreCase(
                        pluginJSON.get("recommended").getAsJsonObject().get("name").getAsString()))
                    continue;

                DirtUpdater.getLogger().warn("\n" + "Update Available For: " + plugin.getName() + "\n" +
                        "Old Version: " + plugin.getVersion().get() + "\n" +
                        "New Version: " + pluginJSON.get("recommended").getAsJsonObject().get("name").getAsString());
                pluginMap.put(plugin, pluginJSON.get("recommended").getAsJsonObject().get("name").getAsString());
            } catch (JsonSyntaxException exception) {
            }
        }
        return pluginMap;
    }
}
