package com.github.aws404.booking_it;

import com.chocohead.mm.api.ClassTinkerers;
import com.chocohead.mm.api.EnumAdder;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EarlyRiser implements Runnable {
    public static List<RecipeBookAdder.RecipeCategoryOptions> ADDED_CATEGORIES = new ArrayList<>();

    @Override
    public void run() {
        switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> loadClient();
            case SERVER -> loadServer();
        }
    }

    private static void loadClient() {
        MappingResolver mappings = FabricLoader.getInstance().getMappingResolver();
        String recipeBookCategory = mappings.mapClassName("intermediary", "net.minecraft.class_5421"); // "net.minecraft.recipe.book.RecipeBookCategory"
        String recipeBookGroup =  mappings.mapClassName("intermediary", "net.minecraft.class_314"); // "net.minecraft.client.recipebook.RecipeBookGroup"
        String itemStackArray = "[L" + mappings.mapClassName("intermediary", "net.minecraft.class_1799")  + ";"; // "[Lnet/minecraft/item/ItemStack;"

        EnumAdder categoryAdder = ClassTinkerers.enumBuilder(recipeBookCategory);
        EnumAdder groupAdder = ClassTinkerers.enumBuilder(recipeBookGroup, itemStackArray);

        FabricLoader.getInstance().getEntrypoints("booking_it:recipe_book", RecipeBookAdder.class).forEach(recipeBookAdder -> recipeBookAdder.getCategories().forEach(categoryOptions -> {
            categoryAdder.addEnum(categoryOptions.name());

            for (RecipeBookAdder.RecipeGroupOptions groupOptions : categoryOptions.groups()) {
                addToEnumBuilder(groupAdder, groupOptions);
            }
            if (categoryOptions.searchGroup() != null) {
                addToEnumBuilder(groupAdder, categoryOptions.searchGroup());
            }

            ADDED_CATEGORIES.add(categoryOptions);
        }));

        categoryAdder.build();
        groupAdder.build();
    }

    private static void loadServer() {
        MappingResolver mappings = FabricLoader.getInstance().getMappingResolver();
        String recipeBookCategory = mappings.mapClassName("intermediary", "net.minecraft.class_5421"); // "net.minecraft.recipe.book.RecipeBookCategory"
        EnumAdder categoryAdder = ClassTinkerers.enumBuilder(recipeBookCategory);

        FabricLoader.getInstance().getEntrypoints("booking_it:recipe_book", RecipeBookAdder.class).forEach(recipeBookAdder -> recipeBookAdder.getCategories().forEach(categoryOptions -> {
            categoryAdder.addEnum(categoryOptions.name());
            ADDED_CATEGORIES.add(categoryOptions);
        }));

        categoryAdder.build();
    }

    private static void addToEnumBuilder(EnumAdder adder, RecipeBookAdder.RecipeGroupOptions groupOptions) {
        adder.addEnum(groupOptions.name().toUpperCase(), () -> new Object[]{Arrays.stream(groupOptions.icons()).map(s -> Registries.ITEM.getOrEmpty(new Identifier(s)).orElseThrow(() -> new IllegalArgumentException(String.format("icon should be an item but found '%s' for group %s", s, groupOptions.name())))).map(ItemStack::new).toArray(ItemStack[]::new)});
    }
}
