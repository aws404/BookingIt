package com.github.aws404.booking_it_example.cooking;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Set;

public class CookingScreen extends AbstractFurnaceScreen<CookingScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/furnace.png");

    public CookingScreen(CookingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, new RecipeBook(), inventory, title, TEXTURE);
    }

    private static class RecipeBook extends AbstractFurnaceRecipeBookScreen {
        private static final Text TOGGLE_SMELTABLE_RECIPES_TEXT = new TranslatableText("gui.recipebook.toggleRecipes.smeltable");

        protected Text getToggleCraftableButtonText() {
            return TOGGLE_SMELTABLE_RECIPES_TEXT;
        }

        @Override
        protected Set<Item> getAllowedFuels() {
            return FurnaceBlockEntity.createFuelTimeMap().keySet();
        }


    }
}
