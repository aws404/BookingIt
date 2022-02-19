package com.github.aws404.booking_it_example.cooking;

import com.github.aws404.booking_it_example.TestMod;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class CookingRecipe extends AbstractCookingRecipe {

    public CookingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(TestMod.TEST_RECIPE_TYPE, id, group, input, output, experience, cookTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TestMod.TEST_RECIPE_SERIALISER;
    }
}
