package net.gardna.james.autotreechopper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EntityListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // loop through all TreeChoppers
        for (int i = 0; i < Main.treeChoppers.size(); i++) {
            Main.treeChoppers.get(i).onPlayerInteractEntity(event);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // loop through all TreeChoppers
        for (int i = 0; i < Main.treeChoppers.size(); i++) {
            Main.treeChoppers.get(i).onEntityDamageByEntity(event);
        }
    }
}
