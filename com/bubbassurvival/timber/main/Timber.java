package com.bubbassurvival.timber.main;

import org.bukkit.plugin.java.*;
import org.bukkit.event.block.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;

public class Timber extends JavaPlugin
{
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.getLogger().info("Loaded config file");
        this.getServer().getPluginManager().registerEvents((Listener)new Listener() {
            @EventHandler
            public void timberIt(final BlockBreakEvent event) {
                final int ih = event.getPlayer().getItemInHand().getTypeId();
                final World tw = event.getPlayer().getWorld();
                final int x = event.getBlock().getX();
                final int y = event.getBlock().getY();
                final int z = event.getBlock().getZ();
                Timber.this.reloadConfig();
                if (event.getBlock().getTypeId() == 17 && Timber.this.getConfig().getIntegerList("timber-items").contains(ih)) {
                    Timber.this.breakChain(tw, x, y, z);
                }
            }
        }, (Plugin)this);
    }
    
    private Integer gb(final World world, final int x, final int y, final int z) {
        return world.getBlockTypeIdAt(x, y, z);
    }
    
    public void breakChain(final World w, final int x, final int y, final int z) {
        final String t = this.getConfig().getString("timber-type");
        if (t.equals("classic") || t.equals("classic-leaves") || t.equals("full") || t.equals("full-noleaves")) {
            final String ty = t;
            w.getBlockAt(x, y, z).breakNaturally();
            if (this.gb(w, x, y + 1, z) == 17) {
                this.breakChain(w, x, y + 1, z);
            }
            if (ty.equals("classic-leaves") || ty.equals("full")) {
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
            if (ty.equals("full") || ty.equals("full-noleaves")) {
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
