package net.gardna.james.autotreechopper;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {
    public static List<TreeChopper> treeChoppers = new ArrayList<>();
    @Override
    public void onEnable() {
        // save default config if it doesn't exist
        saveDefaultConfig();
        // register event listener for BlockPlaceEvent
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        // register event listener for PlayerInteractEntityEvent
        getServer().getPluginManager().registerEvents(new EntityListener(), this);

        getLogger().info("Enabled " + this.getName());
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + this.getName());
    }
}