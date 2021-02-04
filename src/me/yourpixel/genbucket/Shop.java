package me.yourpixel.genbucket;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Shop implements Listener {

	Main m;

	public Shop(Main m) {
		this.m = m;
	}

	public void mainShopPage(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.YELLOW + "GenBucket Shop");
		int i=1;
		for(String x:m.getConfig().getConfigurationSection("items").getKeys(false)) {
			createItem(inv, Material.getMaterial(x), 1, i, ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("items."+x+"displayname"))+"", m.getConfig().getStringList("items."+x+".lore"));
			i++;
		}
		
		p.openInventory(inv);
		return;
	}

	public ItemStack createItem(Inventory inv, Material mat, int ammount, int invslot, String displayName,
			List<String> lorelist) {
		ItemStack item;
		item = new ItemStack(mat, ammount);

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		
		meta.setLore(lorelist);
		item.setItemMeta(meta);
		inv.setItem(invslot - 1, item);

		return item;
	}

	@EventHandler
	public void shopInventory(InventoryClickEvent e) {
		if (e.getCurrentItem() != null)
			if (!isInventoryFull((Player) e.getWhoClicked()) && m.eco.hasAccount((Player) e.getWhoClicked())
					&& e.getCurrentItem().hasItemMeta()) {
				if (e.getCurrentItem().getItemMeta().getLore() != null
						&& e.getCurrentItem().getItemMeta().getLore().get(0) != null
						&& e.getCurrentItem().getItemMeta().getLore().get(0).equalsIgnoreCase("Buy this for 1,000$")) {
					e.setCancelled(true);
					if (m.eco.getBalance((Player) e.getWhoClicked()) >= 1000) {
						give((Player) e.getWhoClicked(), ChatColor.GRAY + "GenBucket [Cobblestone]");
						m.eco.withdrawPlayer((Player) e.getWhoClicked(), 1000);
					}
					return;
				} else if (e.getCurrentItem().getItemMeta().getLore() != null
						&& e.getCurrentItem().getItemMeta().getLore().get(0) != null
						&& e.getCurrentItem().getItemMeta().getLore().get(0).equalsIgnoreCase("Buy this for 15,000$")) {
					e.setCancelled(true);
					if (m.eco.getBalance((Player) e.getWhoClicked()) >= 15000) {
						give((Player) e.getWhoClicked(), ChatColor.DARK_PURPLE + "GenBucket [Obsidian]");
						m.eco.withdrawPlayer((Player) e.getWhoClicked(), 15000);
					}
					if (m.eco.getBalance((Player) e.getWhoClicked()) >= 20000) {
						give((Player) e.getWhoClicked(), ChatColor.YELLOW + "GenBucket [sand]");
						m.eco.withdrawPlayer((Player) e.getWhoClicked(), 20000);
					}
					return;
				}
			}
	}

	public void give(Player p, String a) {
		ItemStack water = new ItemStack(Material.WATER_BUCKET);
		ItemMeta meta = water.getItemMeta();
		meta.setDisplayName(a);
		water.setItemMeta(meta);
		p.getInventory().addItem(water);
	}

	public boolean isInventoryFull(Player p) {
		for (ItemStack a : p.getInventory()) {

			if (a == null || (a.getItemMeta().getDisplayName() != null && (a.getItemMeta().getDisplayName()
					.equalsIgnoreCase(ChatColor.DARK_PURPLE + "GenBucket [Obsidian]")
					|| a.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "GenBucket [Cobblestone]"))))
				return false;
		}
		return true;
	}

}
