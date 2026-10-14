package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.summon.SummonType;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * Declares one item.
 *
 * <p>The chained calls describe what the original wrote into the generated resources (lang, models,
 * recipes, tags and the JEI description). Those files ship with the mod here, so the calls only
 * document the item and add its loot injection.
 *
 * @param <T> the item class
 */
public class FSFItemBuilder<T extends Item> {

    private final FSFItemRegistries registries;

    private final ResourceLocation id;

    private final T item;

    public FSFItemBuilder(FSFItemRegistries registries, ResourceLocation id, T item) {
        this.registries = registries;
        this.id = id;
        this.item = item;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public FSFItemBuilder<T> lootTable(ResourceLocation location, Function<LootTable.Builder, LootPool.Builder> function) {
        this.registries.addLootPool(location, function);
        return this;
    }

    public FSFItemBuilder<T> jeiInfo(int index, String enDesc, String zhDesc) {
        this.registries.language("item." + this.id.toLanguageKey() + "jei.description." + index, enDesc, zhDesc);
        return this;
    }

    public FSFItemBuilder<T> itemLanguage(String en, String zh) {
        this.registries.language("item." + this.id.toLanguageKey(), en, zh);
        return this;
    }

    public FSFItemBuilder<T> itemLanguageTooltip(int index, String en, String zh) {
        this.registries.language("item." + this.id.toLanguageKey() + ".tooltip." + index, en, zh);
        return this;
    }

    public <A extends Summon> FSFItemBuilder<T> summonLanguage(
            SummonType<A> type, String en, String zh) {
        this.registries.language("summon." + type.location().toLanguageKey(), en, zh);
        return this;
    }

    public FSFItemBuilder<T> blockLanguage(String en, String zh) {
        this.registries.language("block." + this.id.toLanguageKey(), en, zh);
        return this;
    }

    public FSFItemBuilder<T> recipeWithLookup(BiConsumer<Object, RecipeOutput> outputConsumer) {
        return this;
    }

    public FSFItemBuilder<T> itemTag(TagKey<Item> tagKey) {
        return this;
    }

    public FSFItemBuilder<T> itemModel() {
        return this;
    }

    public T build() {
        return this.item;
    }
}