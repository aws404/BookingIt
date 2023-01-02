package com.github.aws404.booking_it_example.one_to_one_crafting;

import com.github.aws404.booking_it_example.TestMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Optional;

public class OneToOneCraftingScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {
    private final CraftingInventory input;
    private final CraftingResultInventory result;
    private final PlayerEntity player;

    public OneToOneCraftingScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(TestMod.ONE_TO_ONE_CRAFTING_SCREEN_HANDLER_TYPE, syncId);
        this.input = new CraftingInventory(this, 1, 1);
        this.result = new CraftingResultInventory();
        this.player = playerInventory.player;
        this.addSlot(new OneToOneCraftingResultSlot(playerInventory.player, this.input, this.result, 0, 124, 35));

        this.addSlot(new Slot(this.input, 0, 48, 35));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

    }

    protected static void updateResult(ScreenHandler handler, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory) {
        if (!world.isClient) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<OneToOneRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(TestMod.ONE_TO_ONE_RECIPE_TYPE, craftingInventory, world);
            if (optional.isPresent()) {
                OneToOneRecipe craftingRecipe = optional.get();
                if (resultInventory.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipe)) {
                    itemStack = craftingRecipe.craft(craftingInventory);
                }
            }

            resultInventory.setStack(0, itemStack);
            handler.setPreviousTrackedSlot(0, itemStack);
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack));
        }
    }

    public void onContentChanged(Inventory inventory) {
        updateResult(this, this.player.world, this.player, this.input, this.result);
    }

    public void populateRecipeFinder(RecipeMatcher finder) {
        this.input.provideRecipeInputs(finder);
    }

    public void clearCraftingSlots() {
        this.input.clear();
        this.result.clear();
    }

    public boolean matches(Recipe<? super CraftingInventory> recipe) {
        return recipe.matches(this.input, this.player.world);
    }

    public void close(PlayerEntity player) {
        super.close(player);
        this.dropInventory(player, this.input);
    }

    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemStack = stackInSlot.copy();
            if (index == 0) {
                stackInSlot.getItem().onCraft(stackInSlot, this.player.world, player);
                if (!this.insertItem(stackInSlot, 1, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(stackInSlot, itemStack);
            } else if (index >= 2 && index < 38) {
                if (!this.insertItem(stackInSlot, 1, 2, false)) {
                    if (index < 37) {
                        if (!this.insertItem(stackInSlot, 29, 38, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.insertItem(stackInSlot, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.insertItem(stackInSlot, 2, 38, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (stackInSlot.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, stackInSlot);
            if (index == 0) {
                player.dropItem(stackInSlot, false);
            }
        }

        return itemStack;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.result && super.canInsertIntoSlot(stack, slot);
    }

    public int getCraftingResultSlotIndex() {
        return 0;
    }

    public int getCraftingWidth() {
        return this.input.getWidth();
    }

    public int getCraftingHeight() {
        return this.input.getHeight();
    }

    public int getCraftingSlotCount() {
        return 2;
    }

    public RecipeBookCategory getCategory() {
        return TestMod.COOKING_BOOK_CATEGORY;
    }

    public boolean canInsertIntoSlot(int index) {
        return index != this.getCraftingResultSlotIndex();
    }
}

