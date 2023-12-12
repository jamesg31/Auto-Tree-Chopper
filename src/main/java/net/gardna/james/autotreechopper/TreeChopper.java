package net.gardna.james.autotreechopper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class TreeChopper {
    public Block chest;
    private FallingBlock fallingBlock;
    private ArmorStand armorStand;
    private Location location;
    private Inventory inventory;
    public void init(Block chest) {
        this.chest = chest;

        // create chest inventory and set it to the chest's inventory contents
        inventory = Bukkit.createInventory(null, 27, "Chest");
        inventory.setContents(((Chest) chest.getState()).getBlockInventory().getContents());

        // replace chest with armor stand
        armorStand = chest.getWorld().spawn(chest.getLocation().add(.5, -1.35, .5), ArmorStand.class);
        chest.setType(Material.AIR);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setCanPickupItems(false);
        // set armor stand helmet to chest
        EntityEquipment equipment = armorStand.getEquipment();
        assert equipment != null;
        equipment.setHelmet(new ItemStack(Material.CHEST));

        // spawn falling block underneath chest
        Block bit = chest.getRelative(BlockFace.DOWN);
        location = bit.getLocation();
        fallingBlock = chest.getWorld().spawnFallingBlock(bit.getLocation(), bit.getBlockData());
        fallingBlock.setGravity(false);
    }

    public void destroy() {
        // remove armor stand
        armorStand.remove();
        // remove falling block
        fallingBlock.remove();

        // replace armor stand and falling block with chest and diamond block
        location.getBlock().setType(Material.DIAMOND_BLOCK);
        Block chest = location.add(0, 1, 0).getBlock();
        chest.setType(Material.CHEST);
        // set chest inventory to inventory of tree chopper
        Chest chestState = (Chest) chest.getState();
        chestState.getBlockInventory().setContents(inventory.getContents());

        // remove tree chopper from list of tree choppers
        Main.treeChoppers.remove(this);
    }

    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // if player is interacting with this tree chopper
        if (!event.getRightClicked().equals(armorStand) && !event.getRightClicked().equals(fallingBlock)) {
            return;
        }
        // open chest inventory
        event.getPlayer().openInventory(inventory);
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // if player is attacking with this tree chopper
        if (!event.getEntity().equals(armorStand) && !event.getEntity().equals(fallingBlock)) {
            return;
        }
        // destroy tree chopper
        destroy();
    }
}
