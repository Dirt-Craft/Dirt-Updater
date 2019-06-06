package net.dirtcraft.dirtupdater.Utils;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;

public class EventListeners {

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        Update.checkUpdates();
        DataUtils.logPlugins();
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event){
        Update.checkUpdates();
    }

}
