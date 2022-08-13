package org.mooner.moonerbungeeapi.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.mooner.moonerbungeeapi.db.EventLogDB;

@Deprecated
public class EventLogger implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        EventLogDB.init.log(e.getPlayer(), EventType.JOIN);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        if(e.isCancelled()) return;
        EventLogDB.init.log(e.getPlayer(), EventType.PLACE, e.getBlockPlaced());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMultiPlace(BlockMultiPlaceEvent e) {
        if(e.isCancelled()) return;
        EventLogDB.init.log(e.getPlayer(), EventType.PLACE, e.getBlockPlaced());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        if(e.isCancelled()) return;
        EventLogDB.init.log(e.getPlayer(), EventType.BREAK, e.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if(e.isCancelled()) return;
        EventLogDB.init.log(e.getPlayer(), EventType.PLACE, e.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketFill(PlayerBucketFillEvent e) {
        if(e.isCancelled()) return;
        EventLogDB.init.log(e.getPlayer(), EventType.BREAK, e.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if(e.useInteractedBlock() == Event.Result.DENY || e.useItemInHand() == Event.Result.DENY) return;
        final Block b = e.getClickedBlock();
        if(b != null) EventLogDB.init.log(e.getPlayer(), EventType.INTERACT, b);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if(e.isCancelled()) return;
        EventLogDB.init.log(e.getPlayer(), EventType.INTERACT_ENTITY, e.getRightClicked());
    }

//    @Deprecated
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onTeleport(PlayerTeleportEvent e) {
//        if(e.isCancelled()) return;
//        EventLogDB.init.log(e.getPlayer(), EventType.TELEPORT, e.getFrom(), e.getTo());
//    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if(e.isCancelled() || !(e.getDamager() instanceof Player p)) return;
        EventLogDB.init.log(p, EventType.ATTACK, e.getEntity());
    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onDamage(EntityDamageByBlockEvent e) {
//        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
//        e.setCancelled(true);
//    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onDamage(EntityDamageEvent e) {
//        if(!e.getEntity().getWorld().getName().startsWith("world")) return;
//        e.setCancelled(true);
//    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onClick(InventoryClickEvent e) {
//        final Location loc = e.getInventory().getLocation();
//        if(e.isCancelled() || e.getClickedInventory() == null || e.getCurrentItem() == null || loc == null) return;
//        byte[] map = ItemParser.itemToSerial(e.getCurrentItem().clone());
//        if(e.getClickedInventory().equals(e.getWhoClicked().getInventory())) {
//            EventLogDB.init.log((Player) e.getWhoClicked(), EventType.INVENTORY_CLICK_MY, loc, e.getSlot(), e.getCurrentItem().getType(), map, e.getAction().toString());
//        } else {
//            EventLogDB.init.log((Player) e.getWhoClicked(), EventType.INVENTORY_CLICK_OF, loc, e.getSlot(), e.getCurrentItem().getType(), map, e.getAction().toString());
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onClick(InventoryDragEvent e) {
//        final Location loc = e.getInventory().getLocation();
//        if(e.isCancelled() || e.getClickedInventory() == null || e.getCurrentItem() == null || loc == null) return;
//        byte[] map = ItemParser.itemToSerial(e.getCurrentItem().clone());
//        if(e.getClickedInventory().equals(e.getWhoClicked().getInventory())) {
//            EventLogDB.init.log((Player) e.getWhoClicked(), EventType.INVENTORY_CLICK_MY, loc, e.getSlot(), e.getCurrentItem().getType(), map, e.getAction().toString());
//        } else {
//            EventLogDB.init.log((Player) e.getWhoClicked(), EventType.INVENTORY_CLICK_OF, loc, e.getSlot(), e.getCurrentItem().getType(), map, e.getAction().toString());
//        }
//    }
}
