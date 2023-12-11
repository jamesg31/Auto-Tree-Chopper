package net.gardna.james.autotreechopper;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Enabled " + this.getName());
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + this.getName());
    }
}