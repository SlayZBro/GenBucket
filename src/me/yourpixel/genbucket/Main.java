package me.yourpixel.genbucket;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {
	int schdular;
	public Economy eco = null;
	public Shop s;

	@Override
	public void onEnable() {
		if (!setupEconomy())
			Bukkit.shutdown();
		getServer().getConsoleSender().sendMessage("Genbucket plugin has been enabled");
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new Shop(this), this);

		getConfig().options().copyDefaults(true);
		getConfig().addDefault("items.cobblestone.price", 10000);
		getConfig().addDefault("items.cobblestone.way", "UP");

		ArrayList<String> lore = new ArrayList<>();
		lore.add("First line");
		lore.add("Second line");
		getConfig().addDefault("items.cobblestone.lore", lore);

		getConfig().addDefault("items.cobblestone.displayname", "&aCobbleStone Gen");

		saveConfig();

		s = new Shop(this);
	}

	

	@SuppressWarnings("deprecation")
	@EventHandler
	public void waterPlace(PlayerInteractEvent e) {
		if (e.getItem() != null && e.getItem().getItemMeta().getDisplayName() != null)
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK
					&& e.getPlayer().getItemInHand().getType() == Material.WATER_BUCKET) {
				Location loc = e.getClickedBlock().getLocation();
				FLocation floc = new FLocation(loc);
				Faction fac = Board.getInstance().getFactionAt(floc);
				BlockFace b = e.getBlockFace();
				e.setCancelled(true);

				switch (b) {
				case EAST:
					loc.setX(loc.getX() + 1);
					break;
				case SOUTH:
					loc.setZ(loc.getZ() + 1);
					break;
				case WEST:
					loc.setX(loc.getX() - 1);
					break;
				case NORTH:
					loc.setZ(loc.getZ() - 1);
					break;
				case DOWN:
					loc.setY(loc.getY() - 1);
					break;
				case UP:
					loc.setY(loc.getY() + 1);
					break;
				default:
					break;
				}

				if (FPlayers.getInstance().getByPlayer(e.getPlayer()).getFactionId().equals(fac.getId())) {
					
					for(String x:getConfig().getConfigurationSection("items").getKeys(false)) {
						
						if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "items."+x+"displayname")))
						{
							
							direction d = null;
							if(getConfig().getString("items."+x+".way").equalsIgnoreCase("up"))
								d=direction.UP;
							if(getConfig().getString("items."+x+".way").equalsIgnoreCase("down"))
								d=direction.DOWN;
							if(getConfig().getString("items."+x+".way").equalsIgnoreCase("east"))
								d=direction.EAST;
							if(getConfig().getString("items."+x+".way").equalsIgnoreCase("west"))
								d=direction.WEST;
							if(getConfig().getString("items."+x+".way").equalsIgnoreCase("north"))
								d=direction.NORTH;
							if(getConfig().getString("items."+x+".way").equalsIgnoreCase("south"))
								d=direction.SOUTH;
							
							if(d==null) {
								e.getPlayer().sendMessage("[Genbucket] No direction in config UP/DOWN");
								return;
							}
							cobbleStone(loc, Material.getMaterial(x), d);
							consumeItem(e.getPlayer(), 1, e.getItem());

						}
						
					}
					
//					if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName()
//							.equalsIgnoreCase(ChatColor.GRAY + "genbucket [cobblestone]")) {
//						consumeItem(e.getPlayer(), 1, e.getItem());
//						cobbleStone(loc, Material.COBBLESTONE, direction.DOWN);
//
//					} else if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName()
//							.equalsIgnoreCase(ChatColor.DARK_PURPLE + "genbucket [obsidian]")) {
//						cobbleStone(loc, Material.OBSIDIAN, direction.DOWN);
//						consumeItem(e.getPlayer(), 1, e.getItem());
//					} else if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName()
//							.equalsIgnoreCase(ChatColor.YELLOW + "genbucket [sand]")) {
//						cobbleStone(loc, Material.SAND, direction.UP);
//						consumeItem(e.getPlayer(), 1, e.getItem());
//					}
				}
			}
	}

	public void cobbleStone(Location loc, Material m, direction d) {

		new BukkitRunnable() {

			@Override
			public void run() {
				if (d == direction.DOWN) {
					if (loc.getBlock().getType() == Material.AIR) {
						loc.getBlock().setType(m);
						loc.setY(loc.getY() - 1);
					} else
						this.cancel();
				}

				else if (d == direction.UP) {
					if (loc.getBlock().getType() == Material.AIR) {
						loc.getBlock().setType(m);
						loc.setY(loc.getY() + 1);
					} else
						this.cancel();
				}

			}
		}.runTaskTimer(this, 0, 20);

//		schdular = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
//
//			@Override
//			public void run() {
//
//				if (loc.getBlock().getType() == Material.AIR) {
//					loc.getBlock().setType(m);
//					loc.setY(loc.getY() - 1);
//				} else
//					Bukkit.getScheduler().cancelTask(schdular);
//
//			}
//		}, 0, 20);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player)
			if (label.equalsIgnoreCase("genbucket"))
				s.mainShopPage((Player) sender);

		return true;
	}

	public boolean consumeItem(Player player, int count, ItemStack mat) {
		Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(mat);

		int found = 0;
		for (ItemStack stack : ammo.values())
			found += stack.getAmount();
		if (count > found)
			return false;

		for (Integer index : ammo.keySet()) {
			ItemStack stack = ammo.get(index);

			int removed = Math.min(count, stack.getAmount());
			count -= removed;

			if (stack.getAmount() == removed)
				player.getInventory().setItem(index, null);
			else
				stack.setAmount(stack.getAmount() - removed);

			if (count <= 0)
				break;
		}

		player.updateInventory();
		return true;
	}

	public boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null)
			return false;
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return false;
		eco = rsp.getProvider();
		return true;
	}

}
