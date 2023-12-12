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
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class TreeChopper {
    public Block chest;
    private FallingBlock fallingBlock;
    private ArmorStand fallingBlockArmorStand;
    private ArmorStand armorStand;
    private Vector location;
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
        location = bit.getLocation().toVector();
        fallingBlock = chest.getWorld().spawnFallingBlock(bit.getLocation(), bit.getBlockData());
        fallingBlock.setGravity(false);
        fallingBlockArmorStand = chest.getWorld().spawn(bit.getLocation().add(.5, -2, .5), ArmorStand.class);
        fallingBlockArmorStand.setVisible(false);
        fallingBlockArmorStand.setGravity(false);
        fallingBlockArmorStand.setInvulnerable(true);
        fallingBlockArmorStand.setCanPickupItems(false);
        fallingBlockArmorStand.addPassenger(fallingBlock);

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(Main.getPlugin(Main.class), this::update, 40L, 1L);
    }

    public void destroy() {
        // remove armor stand
        armorStand.remove();
        // remove falling block
        fallingBlockArmorStand.remove();
        fallingBlock.remove();

        // replace armor stand and falling block with chest and diamond block
        location.toLocation(armorStand.getWorld()).getBlock().setType(Material.DIAMOND_BLOCK);
        Block chest = location.add(new Vector(0, 1, 0)).toLocation(armorStand.getWorld()).getBlock();
        chest.setType(Material.CHEST);
        // set chest inventory to inventory of tree chopper
        Chest chestState = (Chest) chest.getState();
        chestState.getBlockInventory().setContents(inventory.getContents());

        // remove tree chopper from list of tree choppers
        Main.treeChoppers.remove(this);
    }

    private void update() {
        // move armor stand and falling block to the chest
        location = location.add(new Vector(.01, 0, 0));
        armorStand.teleport(location.toLocation(armorStand.getWorld()).add(.5, -0.35, .5));
        Bukkit.getLogger().info("location: " + location);

        // move falling block armor stand, must eject and re-add passenger to update position
        fallingBlockArmorStand.eject();
        fallingBlockArmorStand.teleport(location.toLocation(armorStand.getWorld()).add(.5, -2, .5));
        fallingBlockArmorStand.addPassenger(fallingBlock);

        // set falling block to 1 tick lived so it doesn't break
        fallingBlock.setTicksLived(1);
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
