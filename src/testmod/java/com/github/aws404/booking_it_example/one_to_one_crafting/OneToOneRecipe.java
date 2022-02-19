package com.github.aws404.booking_it_example.one_to_one_crafting;

import com.github.aws404.booking_it_example.TestMod;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class OneToOneRecipe implements Recipe<CraftingInventory> {

    private final Identifier id;
    private final Ingredient input;
    private final ItemStack output;

    public OneToOneRecipe(Identifier id, Ingredient input, ItemStack output) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        return this.input.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height == 1;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TestMod.ONE_TO_ONE_RECIPE_SERIALISER;
    }

    @Override
    public RecipeType<?> getType() {
        return TestMod.ONE_TO_ONE_RECIPE_TYPE;
    }

    public static class Serialiser implements RecipeSerializer<OneToOneRecipe> {

        @Override
        public OneToOneRecipe read(Identifier id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(JsonHelper.getObject(json, "input"));
            ItemStack result = new ItemStack(JsonHelper.getItem(json, "result"));
            return new OneToOneRecipe(id, input, result);
        }

        @Override
        public OneToOneRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient input = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();
            return new OneToOneRecipe(id, input, output);
        }

        @Override
        public void write(PacketByteBuf buf, OneToOneRecipe recipe) {
            recipe.input.write(buf);
            buf.writeItemStack(recipe.output);
        }
    }
}
