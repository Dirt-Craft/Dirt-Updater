package net.dirtcraft.dirtupdater.Utils;

import net.dirtcraft.dirtupdater.DirtUpdater;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.scheduler.Task;

public class EventListeners {

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        DataUtils.logPlugins();
        Task.builder()
                .async()
                .execute(Update::checkUpdates)
                .submit(DirtUpdater.getInstance());
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event){
        Update.checkUpdates();
    }

}
