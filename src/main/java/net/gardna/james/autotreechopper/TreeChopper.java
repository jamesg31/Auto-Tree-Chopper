package net.gardna.james.autotreechopper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
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
    private Location location;
    private Inventory inventory;
    private Vector vector;
    // if the tree chopper is in the first half of the block, used for collision detection in update()
    private boolean firstHalf = false;

    /**
     * Initialize the tree chopper
     * @param chest the chest to initialize the tree chopper with
     */
    public void init(Block chest) {
        this.chest = chest;

        // create chest inventory and set it to the chest's inventory contents
        inventory = Bukkit.createInventory(null, 27, "Chest");
        inventory.setContents(((Chest) chest.getState()).getBlockInventory().getContents());

        // get direction of chest
        Directional direction = ((org.bukkit.block.data.type.Chest) chest.getBlockData());

        // convert direction to vector
        vector = direction.getFacing().getDirection();
        // get location of bit block
        Block bit = chest.getRelative(BlockFace.DOWN);
        // set location of tree chopper to bit block
        location = bit.getLocation();
        location.setDirection(direction.getFacing().getDirection());

        Location armorStandLocation = chest.getLocation().add(.5, -1.35, .5);
        armorStandLocation.setDirection(direction.getFacing().getDirection());

        // replace chest with armor stand
        armorStand = chest.getWorld().spawn(bit.getLocation().add(.5, -.35, .5).setDirection(location.getDirection()), ArmorStand.class);
        chest.setType(Material.AIR);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setCanPickupItems(false);
        // set armor stand helmet to chest
        EntityEquipment equipment = armorStand.getEquipment();
        assert equipment != null;
        equipment.setHelmet(new ItemStack(Material.CHEST));

        // replace bit block with falling block and add it to an armor stand
        fallingBlock = chest.getWorld().spawnFallingBlock(bit.getLocation(), bit.getBlockData());
        fallingBlock.setGravity(false);
        fallingBlockArmorStand = chest.getWorld().spawn(bit.getLocation().add(.5, -2, .5).setDirection(location.getDirection()), ArmorStand.class);
        fallingBlockArmorStand.setVisible(false);
        fallingBlockArmorStand.setGravity(false);
        fallingBlockArmorStand.setInvulnerable(true);
        fallingBlockArmorStand.setCanPickupItems(false);
        fallingBlockArmorStand.addPassenger(fallingBlock);

        // schedule update() to run every tick
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(Main.getPlugin(Main.class), this::update, 40L, 1L);
    }

    /**
     * Destroy the tree chopper
     */
    public void destroy() {
        // remove armor stand
        armorStand.remove();
        // remove falling block
        fallingBlockArmorStand.remove();
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

    /**
     * Update the position of the armor stand and falling block, ran on each tick by the scheduler
     */
    private void update() {
        // move armor stand and falling block to the chest
        location = location.add(new Vector(-.1, -.1, -.1).multiply(vector));

        Vector tempVector = location.toVector();
        armorStand.teleport(tempVector.toLocation(armorStand.getWorld()).add(.5, -0.35, .5).setDirection(location.getDirection()));
        Bukkit.getLogger().info("location: " + location + " firstHalf: " + firstHalf);

        // move falling block armor stand, must eject and re-add passenger to update position
        fallingBlockArmorStand.eject();
        fallingBlockArmorStand.teleport(tempVector.toLocation(armorStand.getWorld()).add(.5, -2, .5).setDirection(location.getDirection()));
        fallingBlockArmorStand.addPassenger(fallingBlock);

        // set falling block to 1 tick lived so it doesn't break
        fallingBlock.setTicksLived(1);

        if (location.getX() % 1 > .5 || location.getZ() % 1 > .5) {
            if (firstHalf) {
                Bukkit.getLogger().info("climbOrDescend");
                climbOrDescend();
                firstHalf = false;
            }
        } else {
            firstHalf = true;
        }
    }

    private void climbOrDescend() {
        Vector tempVector = location.toVector().add(new Vector(-1, -1, -1).multiply(vector));
        Block block = tempVector.toLocation(armorStand.getWorld()).getBlock();
        // if block is air, return
        if (block.getType() == Material.AIR) {
            // if block has air underneath it, descend, otherwise return
            if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                location = location.add(0, -1, 0);
                return;
            } else {
                return;
            }
        }
        // if block has a block above it, destroy tree chopper
        if (block.getRelative(BlockFace.UP).getType() != Material.AIR) {
            // cant climb two blocks, so destroy tree chopper
            destroy();
        } else {
            // climb up one block
            location = location.add(0, 1, 0);
        }
    }

    /**
     * Called when a player interacts with an entity (used for right clicks)
     * @param event the event
     */
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // if player is interacting with this tree chopper
        if (!event.getRightClicked().equals(armorStand) && !event.getRightClicked().equals(fallingBlock)) {
            return;
        }
        // open chest inventory
        event.getPlayer().openInventory(inventory);
    }

    /**
     * Called when an entity is damaged by another entity (used for left clicks)
     * @param event the event
     */
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // if player is attacking with this tree chopper
        if (!event.getEntity().equals(armorStand) && !event.getEntity().equals(fallingBlock)) {
            return;
        }
        // destroy tree chopper
        destroy();
    }
}
