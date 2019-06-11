package net.dirtcraft.dirtupdater.Utils;

import net.dirtcraft.dirtupdater.DirtUpdater;
import net.dirtcraft.dirtupdater.OreRepository.List;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
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
        List.listPlugins();
    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        new RegisterCommands();
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event){
        Update.checkUpdates();
    }

}
