package com.github.aws404.booking_it_example;

import com.github.aws404.booking_it_example.cooking.CookingRecipe;
import com.github.aws404.booking_it_example.one_to_one_crafting.OneToOneRecipe;
import com.github.aws404.booking_it.RecipeBookAdder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;

import java.util.List;

public class TestModRecipeBookAdder implements RecipeBookAdder {

    /**
     This is loaded BEFORE the Minecraft classes are, which means you can ONLY reference them inside lambdas,
     otherwise it will break any mixins into that class. This is also the reason that we don't use items directly,
     rather through the identifier.
     */

    @Override
    public List<RecipeCategoryOptions> getCategories() {
        return List.of(
                RecipeBookAdder.builder("COOKING")
                        .addSearch()
                        .addGroup("FOOD", recipe -> recipe instanceof CookingRecipe cookingRecipe && ItemGroups.FOOD_AND_DRINK.contains(cookingRecipe.getOutput().getItem().getDefaultStack()), "minecraft:apple")
                        .addGroup("EVERYTHING_ELSE", recipe -> recipe instanceof CookingRecipe cookingRecipe && !ItemGroups.FOOD_AND_DRINK.contains(cookingRecipe.getOutput().getItem().getDefaultStack()), "minecraft:dirt", "minecraft:sand")
                        .build(),

                RecipeBookAdder.builder("ONE_TO_ONE")
                        .addGroup("ALL", recipe -> recipe instanceof OneToOneRecipe, "minecraft:compass")
                        .build()
        );
    }
}
