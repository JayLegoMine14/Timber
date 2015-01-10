package com.bubbassurvival.timber.main;

import org.bukkit.plugin.java.*;
import org.bukkit.event.block.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.*;

public class Timber extends JavaPlugin
{
    boolean realisticDurability;
    double durabilityMultiplier;
    int blockBreakLimit;
    
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        
        realisticDurability = this.getConfig().getBoolean("realistic-durability");
        durabilityMultiplier = this.getConfig().getDouble("durability-loss-multiplier");
        blockBreakLimit = this.getConfig().getInt("block-break-limit");
        
        this.getLogger().info("Loaded config file");
        this.getServer().getPluginManager().registerEvents((Listener)new Listener() {
            
            @SuppressWarnings("deprecation")
            @EventHandler(priority = EventPriority.HIGHEST)
            public void timberIt(final BlockBreakEvent event) {
                final ItemStack item = event.getPlayer().getItemInHand();
                final int ih = item.getTypeId();
                final World tw = event.getPlayer().getWorld();
                final int x = event.getBlock().getX();
                final int y = event.getBlock().getY();
                final int z = event.getBlock().getZ();
                Timber.this.reloadConfig();
                
                if (event.getBlock().getTypeId() == 17 && Timber.this.getConfig().getIntegerList("timber-items").contains(ih)) {
                    
                    if(event.getPlayer().getGameMode() == GameMode.SURVIVAL){
                        item.setDurability((short) (item.getDurability() + 1));
                        
                        if(item.getDurability() > item.getType().getMaxDurability()) 
                            event.getPlayer().getInventory().clear(event.getPlayer().getInventory().getHeldItemSlot());
                    }
                    
                    MaxDurability = item.getType().getMaxDurability(); 
                    Timber.this.breakChain(tw, x, y, z);
                    
                    if(realisticDurability == true){
                        
                        item.setDurability((short) (item.getDurability() + (DurabilityToRemove * durabilityMultiplier)));
                        if(BlocksBroken > MaxDurability)
                            event.getPlayer().getInventory().clear(event.getPlayer().getInventory().getHeldItemSlot());
                        
                        BlocksBroken = 0;
                        DurabilityToRemove = 0;
                        MaxDurability = 0;
                    }
                }
            }
        }, (Plugin)this);
    }
    
    @SuppressWarnings("deprecation")
    private Integer gb(final World world, final int x, final int y, final int z) {
        return world.getBlockTypeIdAt(x, y, z);
    }
    
    int BlocksBroken = 0;
    int DurabilityToRemove = 0;
    int MaxDurability = 0;
    
    public void breakChain(final World w, final int x, final int y, final int z) {
        final String t = this.getConfig().getString("timber-type");
        if (t.equals("classic") || t.equals("classic-leaves") || t.equals("full") || t.equals("full-noleaves") || t.equals("full-limited")) {
            final String ty = t;
            
            w.getBlockAt(x, y, z).breakNaturally();
            
            if(ty.equals("full-limited")){
                BlocksBroken++; 
                if(BlocksBroken >= blockBreakLimit) return; 
            }
            
            if(realisticDurability == true){
                DurabilityToRemove++;
                if( BlocksBroken > MaxDurability) return;
            }
            
            if (this.gb(w, x, y + 1, z) == 17) {
                this.breakChain(w, x, y + 1, z);
            }
            if (ty.equals("classic-leaves") || ty.equals("full") || ty.equals("full-limited")) {
                if (this.gb(w, x, y + 1, z) == 18) {
                    this.breakChain(w, x, y + 1, z);
                }
                if (this.gb(w, x, y - 1, z) == 18) {
                    this.breakChain(w, x, y - 1, z);
                }
                if (this.gb(w, x + 1, y, z) == 18) {
                    this.breakChain(w, x + 1, y, z);
                }
                if (this.gb(w, x - 1, y, z) == 18) {
                    this.breakChain(w, x - 1, y, z);
                }
                if (this.gb(w, x, y, z + 1) == 18) {
                    this.breakChain(w, x, y, z + 1);
                }
                if (this.gb(w, x, y, z - 1) == 18) {
                    this.breakChain(w, x, y, z - 1);
                }
            }
            if (ty.equals("full") || ty.equals("full-noleaves") || ty.equals("full-limited")) {
                if (this.gb(w, x, y - 1, z) == 17) {
                    this.breakChain(w, x, y - 1, z);
                }
                if (this.gb(w, x + 1, y, z) == 17) {
                    this.breakChain(w, x + 1, y, z);
                }
                if (this.gb(w, x - 1, y, z) == 17) {
                    this.breakChain(w, x - 1, y, z);
                }
                if (this.gb(w, x, y, z + 1) == 17) {
                    this.breakChain(w, x, y, z + 1);
                }
                if (this.gb(w, x, y, z - 1) == 17) {
                    this.breakChain(w, x, y, z - 1);
                }
            }
        }
    }
}
