package com.github.aws404.booking_it_example.one_to_one_crafting;

import com.github.aws404.booking_it_example.TestMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.collection.DefaultedList;

public class OneToOneCraftingResultSlot extends CraftingResultSlot {
    private final CraftingInventory input;
    private final PlayerEntity player;

    public OneToOneCraftingResultSlot(PlayerEntity player, CraftingInventory input, Inventory inventory, int index, int x, int y) {
        super(player, input, inventory, index, x, y);
        this.input = input;
        this.player = player;
    }

    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        DefaultedList<ItemStack> defaultedList = player.world.getRecipeManager().getRemainingStacks(TestMod.ONE_TO_ONE_RECIPE_TYPE, this.input, player.world);

        for(int i = 0; i < defaultedList.size(); ++i) {
            ItemStack itemStack = this.input.getStack(i);
            ItemStack itemStack2 = (ItemStack)defaultedList.get(i);
            if (!itemStack.isEmpty()) {
                this.input.removeStack(i, 1);
                itemStack = this.input.getStack(i);
            }

            if (!itemStack2.isEmpty()) {
                if (itemStack.isEmpty()) {
                    this.input.setStack(i, itemStack2);
                } else if (ItemStack.areItemsEqualIgnoreDamage(itemStack, itemStack2) && ItemStack.areNbtEqual(itemStack, itemStack2)) {
                    itemStack2.increment(itemStack.getCount());
                    this.input.setStack(i, itemStack2);
                } else if (!this.player.getInventory().insertStack(itemStack2)) {
                    this.player.dropItem(itemStack2, false);
                }
            }
        }

    }
}
