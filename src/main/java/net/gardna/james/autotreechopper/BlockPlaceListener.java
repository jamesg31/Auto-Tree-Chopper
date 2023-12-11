package net.gardna.james.autotreechopper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // if block is a chest
        if (event.getBlock().getType() != Material.CHEST) {
            return;
        }
        // get block underneath chest
        Block block = event.getBlock().getRelative(BlockFace.DOWN);
        if (block.getType() != Material.DIAMOND_BLOCK) {
            return;
        }
        event.getPlayer().sendMessage("You placed a chest on top of diamonds!");
    }
}
