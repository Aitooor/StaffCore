package online.nasgar.staffcore.utils;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder implements Listener {

    private ItemStack is;
    private ItemMeta meta;

    public ItemBuilder(Material mat) {
        this.is = new ItemStack(mat);
    }

    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    public ItemBuilder amount(int amount) {
        this.is.setAmount(amount);
        return this;
    }

    public ItemBuilder setOwner(String name) {
        SkullMeta meta = (SkullMeta) this.is.getItemMeta();
        meta.setOwner(name);
        this.is.setItemMeta((ItemMeta) meta);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String name) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> lore = (List<String>) meta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        }
        lore.add(name);
        meta.setLore(CC.translate(lore));
        this.is.setItemMeta(meta);
        return this;
    }

    public List<String> getLore(ItemStack stack) {
        if (stack.getItemMeta() == null) return new ArrayList<>();
        if (stack.getItemMeta().getLore() == null) new ArrayList<>();

        return stack.getItemMeta().getLore();
    }
    public ItemBuilder lore(List<String> lore) {
        List<String> toSet = new ArrayList<String>();
        ItemMeta meta = this.is.getItemMeta();
        if (this.is.getItemMeta().getLore() != null) {
            List<String> oldLores = new ArrayList<String>(this.is.getItemMeta().getLore());
            toSet.addAll(oldLores);
        }
        for (String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        meta.setLore(CC.translate(toSet));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        List<String> toSet = new ArrayList<String>();
        ItemMeta meta = this.is.getItemMeta();
        if (this.is.getItemMeta().getLore() != null) {
            List<String> oldLores = new ArrayList<String>(this.is.getItemMeta().getLore());
            toSet.addAll(oldLores);
        }
        for (String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        meta.setLore(CC.translate(toSet));
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(int durability) {
        this.is.setDurability((short) durability);
        return this;
    }

    public ItemBuilder data(int data) {
        this.is.setData(new MaterialData(this.is.getType(), (byte) data));
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        this.is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(Material material) {
        this.is.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = this.is.getItemMeta();
        meta.setLore(new ArrayList<>());
        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (Enchantment e : this.is.getEnchantments().keySet()) {
            this.is.removeEnchantment(e);
        }
        return this;
    }

    public ItemBuilder enchant(Enchantment enchanement, int level, boolean ignoreLevelRestriction) {
        this.meta.addEnchant(enchanement, level, ignoreLevelRestriction);
        return this;
    }

    public ItemBuilder color(org.bukkit.Color color) {
        if (this.is.getType() == Material.LEATHER_BOOTS || this.is.getType() == Material.LEATHER_CHESTPLATE || this.is.getType() == Material.LEATHER_HELMET || this.is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) this.is.getItemMeta();
            meta.setColor(color);
            this.is.setItemMeta(meta);
            return this;
        }
        throw new IllegalArgumentException("color() only applicable for leather armor!");
    }

    public ItemBuilder repair() {
        this.is.setDurability((short) 0);
        return this;
    }

    public ItemStack build() {
        return this.is;
    }
}
