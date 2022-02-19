package com.github.aws404.booking_it.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RecipeBookOptions.class)
public interface RecipeBookOptionsAccessor {
    @Accessor
    static Map<RecipeBookCategory, Pair<String, String>> getCATEGORY_OPTION_NAMES() {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor
    static void setCATEGORY_OPTION_NAMES(Map<RecipeBookCategory, Pair<String, String>> CATEGORY_OPTION_NAMES) {
        throw new UnsupportedOperationException();
    }
}
