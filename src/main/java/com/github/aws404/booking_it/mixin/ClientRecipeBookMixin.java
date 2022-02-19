package com.github.aws404.booking_it.mixin;

import com.github.aws404.booking_it.BookingIt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Inject(method = "getGroupForRecipe", at = @At("HEAD"), cancellable = true)
    private static void addCustomGroups(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        RecipeBookGroup group = BookingIt.getGroupForRecipe(recipe);
        if (group != null) {
            cir.setReturnValue(group);
        }
    }

}
