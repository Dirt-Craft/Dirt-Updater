package net.dirtcraft.dirtupdater.Configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class PluginConfiguration {
    @Setting(value = "Main")
    private PluginConfiguration.Main main = new PluginConfiguration.Main();


    @ConfigSerializable
    public static class Main {
        @Setting
        public static HashMap<String, Integer> Projects = new HashMap<>();

        @Setting(value = "Should-Update", comment = "Should the project be updated/downloaded?")
        public static HashMap<String, Boolean> Update = new HashMap<>();

        @Setting(value = "Tracked-Mods", comment = "Mods to update automatically.")
        public static HashMap<String, String> Mods = new HashMap<>();
    }
}
