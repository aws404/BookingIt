package com.github.aws404.booking_it_example.cooking;

import com.github.aws404.booking_it_example.TestMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class CookingBlockEntity extends AbstractFurnaceBlockEntity {
    public CookingBlockEntity(BlockPos pos, BlockState state) {
        super(TestMod.TEST_COOKING_BLOCK_ENTITY, pos, state, TestMod.TEST_RECIPE_TYPE);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("conatiner.test");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CookingScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
