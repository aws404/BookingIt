package com.github.aws404.booking_it;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.aws404.booking_it.mixin.RecipeBookGroupAccessor;
import com.github.aws404.booking_it.mixin.RecipeBookOptionsAccessor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBookCategory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookingIt implements ModInitializer, ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("Booking It");
	static Map<RecipeBookCategory, RecipeBookAdder.RecipeCategoryOptions> CATEGORY_TO_OPTIONS_MAP;
	@Environment(EnvType.CLIENT)
	static Map<RecipeBookGroup, RecipeBookAdder.RecipeGroupOptions> GROUP_TO_OPTIONS_MAP;

	@Override
	public void onInitialize() {
		BookingIt.CATEGORY_TO_OPTIONS_MAP = EarlyRiser.ADDED_CATEGORIES.stream().collect(Collectors.toMap(category -> ClassTinkerers.getEnum(RecipeBookCategory.class, category.name()), category -> category));

		LOGGER.info("Loaded {} custom recipe book categories ({}).", CATEGORY_TO_OPTIONS_MAP.size(), CATEGORY_TO_OPTIONS_MAP.keySet());

		// Add the required NBT keys for the category
		ImmutableMap.Builder<RecipeBookCategory, Pair<String, String>> builder = ImmutableMap.<RecipeBookCategory, Pair<String, String>>builder().putAll(RecipeBookOptionsAccessor.getCATEGORY_OPTION_NAMES());
		BookingIt.CATEGORY_TO_OPTIONS_MAP.keySet().forEach(s -> builder.put(s, Pair.of("isGuiOpen_" + s, "isFilteringCraftable_" + s)));
		RecipeBookOptionsAccessor.setCATEGORY_OPTION_NAMES(builder.build());
	}

	@Override
	public void onInitializeClient() {
		BookingIt.GROUP_TO_OPTIONS_MAP = BookingIt.CATEGORY_TO_OPTIONS_MAP.values().stream().flatMap(recipeCategoryOptions -> recipeCategoryOptions.groups().stream()).collect(Collectors.toMap(groupOptions -> ClassTinkerers.getEnum(RecipeBookGroup.class, groupOptions.name()), groupOptions -> groupOptions));

		// Add to the Recipe Group Search Map
		ImmutableMap.Builder<RecipeBookGroup, List<RecipeBookGroup>> groupBuilder = ImmutableMap.<RecipeBookGroup, List<RecipeBookGroup>>builder().putAll(RecipeBookGroup.SEARCH_MAP);
		CATEGORY_TO_OPTIONS_MAP.forEach((category, options) -> {
			if (options.searchGroup() != null) {
				groupBuilder.put(ClassTinkerers.getEnum(RecipeBookGroup.class, options.searchGroup().name()), options.groups().stream().map(group -> ClassTinkerers.getEnum(RecipeBookGroup.class, group.name())).toList());
			}
		});
		RecipeBookGroupAccessor.setSEARCH_MAP(groupBuilder.build());
	}

	/**
	 * @param name the name of the category as defined in the {@link RecipeBookAdder}
	 * @return <code>true</code> is the category has been added.
	 */
	public static boolean isModdedCategory(RecipeBookCategory name) {
		return BookingIt.CATEGORY_TO_OPTIONS_MAP.containsKey(name);
	}

	/**
	 * Transform a category name into the {@link RecipeBookCategory} enum object.
	 * @param name the name of the category as defined in the {@link RecipeBookAdder}
	 * @return the {@link RecipeBookCategory}
	 * @throws IllegalArgumentException If no entry with the given name can be found
	 */
	public static RecipeBookCategory getCategory(String name) {
		return ClassTinkerers.getEnum(RecipeBookCategory.class, name.toUpperCase());
	}

	/**
	 * Transform a group name into the {@link RecipeBookGroup} enum object.
	 * @param category the category the group belongs too. {@link BookingIt#getCategory}
	 * @param name the name of the group as defined in the {@link RecipeBookAdder}
	 * @return the {@link RecipeBookGroup}
	 * @throws IllegalArgumentException If no entry with the given name can be found
	 */
	@Environment(EnvType.CLIENT)
	public static RecipeBookGroup getGroup(RecipeBookCategory category, String name) {
		return ClassTinkerers.getEnum(RecipeBookGroup.class, category.name() + "_" + name.toUpperCase());
	}

	/**
	 * Transform a group name into the {@link RecipeBookGroup} enum object.
	 * @param category the name of the category the group belongs too, as defined in the {@link RecipeBookAdder}
	 * @param name the name of the group as defined in the {@link RecipeBookAdder}
	 * @return the {@link RecipeBookGroup}
	 * @throws IllegalArgumentException If no entry with the given name can be found
	 */
	@Environment(EnvType.CLIENT)
	public static RecipeBookGroup getGroup(String category, String name) {
		return ClassTinkerers.getEnum(RecipeBookGroup.class, category.toUpperCase() + "_" + name.toUpperCase());
	}

	/**
	 * Get the groups that belong to the specified category, excluding the search category.
	 * @param category the category the groups belong too. {@link BookingIt#getCategory}
	 * @return all groups of category
	 */
	@Environment(EnvType.CLIENT)
	public static List<RecipeBookGroup> getGroupsForCategory(RecipeBookCategory category) {
		if (!BookingIt.CATEGORY_TO_OPTIONS_MAP.containsKey(category)) {
			throw new IllegalArgumentException(category + " is not a registered custom category");
		}
		return BookingIt.CATEGORY_TO_OPTIONS_MAP.get(category).groups().stream().map(groupOptions -> ClassTinkerers.getEnum(RecipeBookGroup.class, groupOptions.name())).toList();
	}

	/**
	 * Get all the groups that belong to the specified category.
	 * @param category the category the groups belong too. {@link BookingIt#getCategory}
	 * @return all groups of category
	 */
	@Environment(EnvType.CLIENT)
	public static List<RecipeBookGroup> getGroupsAndSearchForCategory(RecipeBookCategory category) {
		if (!BookingIt.CATEGORY_TO_OPTIONS_MAP.containsKey(category)) {
			throw  new IllegalArgumentException(category + " is not a registered custom category");
		}
		return BookingIt.CATEGORY_TO_OPTIONS_MAP.get(category).allGroupsAndSearch().stream().map(groupOptions -> ClassTinkerers.getEnum(RecipeBookGroup.class, groupOptions.name())).toList();
	}

	/**
	 * Get the custom group a recipe belongs too, or null.
	 * @param recipe the recipe to query
	 * @return the group or <code>null</code> if none.
	 */
	@Nullable
	@Environment(EnvType.CLIENT)
	public static RecipeBookGroup getGroupForRecipe(Recipe<?> recipe) {
		for (Map.Entry<RecipeBookGroup, RecipeBookAdder.RecipeGroupOptions> entry : BookingIt.GROUP_TO_OPTIONS_MAP.entrySet()) {
			if (entry.getValue().recipeSelectionCriteria().test(recipe)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
