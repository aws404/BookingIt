package com.github.aws404.booking_it.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.aws404.booking_it.BookingIt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.book.RecipeBookCategory;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(RecipeBookGroup.class)
public class RecipeBookGroupMixin {
    @Inject(method = "getGroups", at = @At("HEAD"), cancellable = true)
    private static void inject(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        if (BookingIt.isModdedCategory(category)) {
            cir.setReturnValue(BookingIt.getGroupsAndSearchForCategory(category));
        }
    }
}
