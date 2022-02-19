package com.github.aws404.booking_it_example.cooking;

import com.github.aws404.booking_it_example.TestMod;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;

public class CookingScreenHandler extends AbstractFurnaceScreenHandler {
    public CookingScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(TestMod.TEST_COOKING_SCREEN_HANDLER_TYPE, TestMod.TEST_RECIPE_TYPE, TestMod.COOKING_BOOK_CATEGORY, syncId, playerInventory);
    }

    public CookingScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(TestMod.TEST_COOKING_SCREEN_HANDLER_TYPE, TestMod.TEST_RECIPE_TYPE, TestMod.COOKING_BOOK_CATEGORY, syncId, playerInventory, inventory, propertyDelegate);
    }
}
