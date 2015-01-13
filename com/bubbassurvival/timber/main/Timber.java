package com.bubbassurvival.timber.main;

import org.bukkit.plugin.java.*;
import org.bukkit.block.Block;
import org.bukkit.event.block.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.*;

public class Timber extends JavaPlugin
{
    int blockBreakLimit;
    
   final Material Log = Material.LOG;
   final Material Log2 = Material.LOG_2;
   
   final Material Leaves = Material.LEAVES;
   final Material Leaves2 = Material.LEAVES_2;
    
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        
        this.getLogger().info("Loaded config file.");
        this.getServer().getPluginManager().registerEvents((Listener)new Listener() {
            
            @SuppressWarnings("deprecation")
            @EventHandler(priority = EventPriority.LOWEST)
            public void timberIt(final BlockBreakEvent event) {
                
                final ItemStack item = event.getPlayer().getItemInHand();
                final int ih = item.getTypeId();
                final World tw = event.getPlayer().getWorld();
                final int x = event.getBlock().getX();
                final int y = event.getBlock().getY();
                final int z = event.getBlock().getZ();
                Timber.this.reloadConfig();
                
                BlocksBroken = 0;
                if (isLog(event.getBlock()) && Timber.this.getConfig().getIntegerList("timber-items").contains(ih)){
                    
                    if(event.getPlayer().getGameMode() == GameMode.SURVIVAL){
                        item.setDurability((short) (item.getDurability() + 1));
                        
                        if(item.getDurability() > item.getType().getMaxDurability()) 
                            event.getPlayer().getInventory().clear(event.getPlayer().getInventory().getHeldItemSlot());
                    }
                    
                    Timber.this.breakChain(tw, x, y, z);
                }
            }
        }, (Plugin)this);
    }
    
    private boolean isLog(Block b){
        return isLog(b.getWorld(), b.getX(), b.getY(), b.getZ());
    }
    
    private boolean isLog(final World world, final int x, final int y, final int z){
        Material bt = world.getBlockAt(x, y, z).getType();
        return bt == Log || bt == Log2 ;
    }
    
    private boolean isLeaves(final World world, final int x, final int y, final int z){
        Material bt = world.getBlockAt(x, y, z).getType();
        return bt == Leaves || bt == Leaves2 ;
    }
    
    @Override
    public void reloadConfig(){
        super.reloadConfig();
        blockBreakLimit = this.getConfig().getInt("block-break-limit");
    }
    
    int BlocksBroken = 0;
    
    public void breakChain(final World w, final int x, final int y, final int z) {
        final String t = this.getConfig().getString("timber-type");
        if (t.equals("classic") || t.equals("classic-leaves") || t.equals("full") || t.equals("full-noleaves") || t.equals("full-limited")) {
            final String ty = t;
            
            w.getBlockAt(x, y, z).breakNaturally();
            
            if(ty.equals("full-limited")){
                BlocksBroken++; 
                if(BlocksBroken >= blockBreakLimit) return; 
            }
            
            if (this.isLog(w, x, y + 1, z)) {
                this.breakChain(w, x, y + 1, z);
            }
            if (ty.equals("classic-leaves") || ty.equals("full") || ty.equals("full-limited")) {
                if (this.isLeaves(w, x, y + 1, z)) {
                    this.breakChain(w, x, y + 1, z);
                }
                if (this.isLeaves(w, x, y - 1, z)) {
                    this.breakChain(w, x, y - 1, z);
                }
                if (this.isLeaves(w, x + 1, y, z)) {
                    this.breakChain(w, x + 1, y, z);
                }
                if (this.isLeaves(w, x - 1, y, z)) {
                    this.breakChain(w, x - 1, y, z);
                }
                if (this.isLeaves(w, x, y, z + 1)) {
                    this.breakChain(w, x, y, z + 1);
                }
                if (this.isLeaves(w, x, y, z - 1)) {
                    this.breakChain(w, x, y, z - 1);
                }
            }
            if (ty.equals("full") || ty.equals("full-noleaves") || ty.equals("full-limited")) {
                if (this.isLog(w, x, y - 1, z)) {
                    this.breakChain(w, x, y - 1, z);
                }
                if (this.isLog(w, x + 1, y, z)) {
                    this.breakChain(w, x + 1, y, z);
                }
                if (this.isLog(w, x - 1, y, z)) {
                    this.breakChain(w, x - 1, y, z);
                }
                if (this.isLog(w, x, y, z + 1)) {
                    this.breakChain(w, x, y, z + 1);
                }
                if (this.isLog(w, x, y, z - 1)) {
                    this.breakChain(w, x, y, z - 1);
                }
            }
        }
    }
}
