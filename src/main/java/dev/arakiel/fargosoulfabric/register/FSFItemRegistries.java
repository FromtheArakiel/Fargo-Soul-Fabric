package dev.arakiel.fargosoulfabric.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * Item registry of one mod.
 *
 * <p>Items are registered straight into {@code minecraft:item} while the mod initializes. The
 * builder that is handed out keeps the pieces the original used for its data generation (loot
 * injection and the generated lang, model and recipe files); the resources ship with the mod now,
 * so only the loot injection is wired up to the game.
 */
public final class FSFItemRegistries {

    /** All registries, by mod id, so the loot injection can walk them. */
    public static final Map<String, FSFItemRegistries> REGISTRIES = new ConcurrentHashMap<>();

    /** Loot pools the mod adds to vanilla loot tables, by table id. */
    public final Map<ResourceLocation, List<Function<LootTable.Builder, LootPool.Builder>>> lootTableData = new HashMap<>();

    /** Everything this mod registers, in registration order. */
    private final List<Item> registeredItems = new ArrayList<>();

    private final String modid;

    private FSFItemRegistries(String modid) {
        this.modid = modid;
        REGISTRIES.put(modid, this);
    }

    public static FSFItemRegistries create(String modid) {
        return new FSFItemRegistries(modid);
    }

    public String modid() {
        return this.modid;
    }

    public <T extends Item> FSFItemBuilder<T> build(String name, Function<ResourceLocation, T> function) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.modid, name);
        T item = function.apply(id);
        Registry.register(BuiltInRegistries.ITEM, id, item);
        this.registeredItems.add(item);
        return new FSFItemBuilder<>(this, id, item);
    }

    /** The items of this mod, in the order they were registered. */
    public List<Item> items() {
        return java.util.Collections.unmodifiableList(this.registeredItems);
    }

    public <T extends Item> FSFItemBuilder<T> build(String name, Supplier<T> supplier) {
        return this.build(name, location -> supplier.get());
    }

    /** The strings live in the lang files of the mod; this only keeps the source readable. */
    public void language(String key, String en, String zh) {
    }

    /** Same as {@link #language}: the texts are shipped as lang files. */
    public void addLootPool(ResourceLocation table, Function<LootTable.Builder, LootPool.Builder> pool) {
        this.lootTableData.computeIfAbsent(table, key -> new ArrayList<>()).add(pool);
    }
}