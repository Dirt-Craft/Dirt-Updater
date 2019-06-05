package dirtcraft.dirtupdater;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import dirtcraft.dirtupdater.data.DataUtils;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "dirt-updater",
        name = "Dirt Updater",
        authors = {
                "Juliann",
                "ExperiencedDev/MrDj200"
        }
)
public class DirtUpdater {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        // This is just for testing purposes of course
        logger.info("[Dirt-Updater] Testing stuff now...");
        logger.info(DataUtils.getStringFromURL("https://ci.whipit.de/job/DCTest/lastSuccessfulBuild/api/json"));
        JsonObject js = DataUtils.getJsonObjFromString(DataUtils.getStringFromURL("https://ci.whipit.de/job/DCTest/lastSuccessfulBuild/api/json"));
        if(!js.isJsonObject()){
            logger.error("[Dirt-Updater] Website is not returning a Json string");
        }
        logger.info("[Dirt-Updater] Testing stuff is now complete.");
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event){
        logger.info("[Dirt-Updater] Checking for updates...");
    }
}
