package me.fckml.staffcore.staffmode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import me.fckml.staffcore.StaffCore;
import me.fckml.staffcore.utils.CC;
import me.fckml.staffcore.utils.ItemBuilder;
import me.fckml.staffcore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class StaffModeManager implements Listener {

    @Getter private static StaffModeManager instance;

    private Map<UUID, Long> freezeTimer;
    private Map<UUID, ItemStack[]> armorMap;
    private Map<UUID, ItemStack[]> itemMap;

    private List<UUID> frozenPlayers, vanishedPlayers, inStaffMode;

    public StaffModeManager() {
        instance = this;

        this.frozenPlayers = Lists.newArrayList();
        this.vanishedPlayers = Lists.newArrayList();
        this.inStaffMode = Lists.newArrayList();

        this.armorMap = Maps.newConcurrentMap();
        this.itemMap = Maps.newConcurrentMap();
        this.freezeTimer = Maps.newConcurrentMap();

        Bukkit.getPluginManager().registerEvents(this, StaffCore.getInstance());
    }

    public void freezePlayer(Player player) {
        StaffCore.getInstance().getMessageHandler().getMany(player, "FREEZE.FREEZE_MESSAGE").getContents().forEach(line -> player.sendMessage(CC.translate(line)));

        this.freezeTimer.put(player.getUniqueId(), System.currentTimeMillis());
        this.frozenPlayers.add(player.getUniqueId());
    }

    public void unFreezePlayer(Player player) {
        StaffCore.getInstance().getMessageHandler().getMany(player, "FREEZE.UNFREEZE_MESSAGE").getContents().forEach(line -> player.sendMessage(CC.translate(line)));

        this.freezeTimer.remove(player.getUniqueId());
        this.frozenPlayers.remove(player.getUniqueId());
    }

    public void setVanished(Player player, boolean vanished) {
        if (vanished) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("core.command.vanish")) continue;
                if (online.equals(player)) continue;

                online.hidePlayer(player);
            }

            player.sendMessage(CC.translate(StaffCore.getInstance().getMessageHandler().get(player, "VANISH.ENABLED")));

            this.vanishedPlayers.add(player.getUniqueId());
            return;
        }

        for (Player online : Bukkit.getOnlinePlayers()) online.showPlayer(player);

        player.sendMessage(CC.translate(StaffCore.getInstance().getMessageHandler().get(player, "VANISH.DISABLED")));

        this.vanishedPlayers.remove(player.getUniqueId());
    }

    public boolean isVanished(Player player) {
        return this.vanishedPlayers.contains(player.getUniqueId());
    }

    public void setInStaffMode(Player player, boolean staffMode) {
        if (staffMode) {
            this.itemMap.put(player.getUniqueId(), player.getInventory().getContents());
            this.armorMap.put(player.getUniqueId(), player.getInventory().getArmorContents());

            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);

            player.getInventory().clear();

            player.setGameMode(GameMode.CREATIVE);

            player.getInventory().setItem(0, new ItemBuilder(Material.INK_SACK).durability(10).name("&b&lVanish &7(&aON&7)").build());

            player.getInventory().setItem(2, new ItemBuilder(Material.REDSTONE).name("&cDisable Staff Mode &7(Right Click)").build());
            player.getInventory().setItem(3, new ItemBuilder(Material.SUGAR).name("&bSpeed &7(Right Click)").build());
            player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).name("&eFly &7(Right Click)").build());
            player.getInventory().setItem(5, new ItemBuilder(Material.PACKED_ICE).name("&bFreeze &7(Right Click)").build());
            player.getInventory().setItem(6, new ItemBuilder(Material.SKULL_ITEM).setOwner(player.getName()).name("&bOnline Staff &7(Right Click)").build());

            player.getInventory().setItem(8, new ItemBuilder(Material.CHEST).name("&bCore Protect Addon &7(Right Click)").build());

            if (!this.isVanished(player)) this.setVanished(player, true);
            player.sendMessage(CC.translate(StaffCore.getInstance().getMessageHandler().get(player, "STAFFMODE.ENABLED")));

            this.inStaffMode.add(player.getUniqueId());
            return;
        }

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        player.getInventory().clear();

        player.setGameMode(GameMode.SURVIVAL);

        player.getInventory().setContents(this.itemMap.remove(player.getUniqueId()));
        player.getInventory().setArmorContents(this.armorMap.remove(player.getUniqueId()));

        this.setVanished(player, false);
        player.sendMessage(CC.translate(StaffCore.getInstance().getMessageHandler().get(player,"STAFFMODE.DISABLED")));
        this.inStaffMode.remove(player.getUniqueId());
    }

    public boolean isInStaffMode(Player player) {
        return this.inStaffMode.contains(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onRightClickPlayer(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!this.inStaffMode.contains(player.getUniqueId())) return;
        if (!(event.getRightClicked() instanceof Player)) return;
        if (player.getItemInHand() == null) return;
        if (player.getItemInHand().getType() != Material.PACKED_ICE) return;

        Player rightClicked = (Player) event.getRightClicked();

        player.performCommand("freeze " + rightClicked.getName());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!this.inStaffMode.contains(player.getUniqueId())) return;
        if (player.isOp()) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        StaffModeManager.getInstance().setInStaffMode(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!this.inStaffMode.contains(player.getUniqueId())) return;
        ItemStack stack = event.getItem();
        if (stack == null) return;

        switch (stack.getType()) {
            case INK_SACK: {
                if (player.getItemInHand().getDurability() == 10) {
                    player.getInventory().setItem(0, new ItemBuilder(Material.INK_SACK).durability(8).name("&b&lVanish &7(&cOFF&7)").build());

                    this.setVanished(player, false);
                    return;
                }

                player.getInventory().setItem(0, new ItemBuilder(Material.INK_SACK).durability(10).name("&b&lVanish &7(&aON&7)").build());

                this.setVanished(player, true);
                return;
            }
            case REDSTONE: {
                this.setInStaffMode(player, false);
                return;
            }
            case NETHER_STAR: {
                if (player.getAllowFlight()) {
                    player.setAllowFlight(false);
                    player.sendMessage(CC.translate(StaffCore.getInstance().getMessageHandler().get(player, "FLY.DISABLED")));
                    return;
                }

                player.setAllowFlight(true);
                player.sendMessage(CC.translate(StaffCore.getInstance().getMessageHandler().get(player, "FLY.ENABLED")));
                return;
            }
            case SUGAR: {
                if (player.getFlySpeed() == 0.2f) {
                    player.setFlySpeed(0.4f);
                    return;
                }

                if (player.getFlySpeed() == 0.4f) {
                    player.setFlySpeed(0.6f);
                    return;
                }

                if (player.getFlySpeed() == 0.6f) {
                    player.setFlySpeed(0.8f);
                    return;
                }

                if (player.getFlySpeed() == 0.8) {
                    player.setFlySpeed(1.0f);
                    return;
                }

                player.setFlySpeed(0.2f);
                return;
            }
            case CHEST: {
                player.performCommand("co i");
                return;
            }
            case SKULL_ITEM: {
                Tasks.runAsyncTask(() -> {
                    Inventory inventory = Bukkit.createInventory(null, 27, CC.translate("Staff Online"));

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (!online.hasPermission("core.staff")) continue;

                        inventory.addItem(new ItemBuilder(Material.SKULL_ITEM).durability(3).setOwner(online.getName()).name("&b" + online.getName()).build());
                    }

                    player.openInventory(inventory);
                });
            }
        }
    }
}
