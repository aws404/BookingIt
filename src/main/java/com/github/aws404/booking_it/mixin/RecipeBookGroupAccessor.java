package com.github.aws404.booking_it.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.recipebook.RecipeBookGroup;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(RecipeBookGroup.class)
public interface RecipeBookGroupAccessor {
    @Mutable
    @Accessor("SEARCH_MAP")
    static void setSEARCH_MAP(Map<RecipeBookGroup, List<RecipeBookGroup>> SEARCH_MAP) {
        throw new UnsupportedOperationException();
    }
}
