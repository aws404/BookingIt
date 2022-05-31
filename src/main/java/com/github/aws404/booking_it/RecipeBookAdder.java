package com.github.aws404.booking_it;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * To add recipe book categories, you need to create a class which implements RecipeBookAdder,
 * then add it to your <code>fabric.mod.json</code> under the entrypoint <code>booking_it:recipe_book</code>.
 *
 * A recipe book CATEGORY is equivalent to the type of recipe, for example: CRAFTING, SMELTING, BLASTING, ect. See {@link net.minecraft.recipe.book.RecipeBookCategory} for more.
 * A recipe book GROUP are the tabs that appear on the side of the book, for example: CRAFTING_BUILDING_BLOCKS, CRAFTING_REDSTONE, CRAFTING_MISC ect. See {@link net.minecraft.client.recipebook.RecipeBookGroup} for more. Note that these are ONLY on the client.
 *
 * This is loaded BEFORE the Minecraft classes are, which means you can ONLY reference them inside lambdas,
 * otherwise it will break any mixins into that class. This is also the reason that we don't use items directly,
 * rather through the identifier.
 */
public interface RecipeBookAdder {

    /**
     * @return a list of categories (build with {@link RecipeCategoryBuilder}) to add to the recipe book.
     */
    List<RecipeCategoryOptions> getCategories();

    /**
     * Create a new builder for a category
     * @param name the name of the category (must be unique and follow enum naming rules, ie. no <code>.:';"</code>, ect)
     */
    static RecipeCategoryBuilder builder(String name) {
        return new RecipeCategoryBuilder(name);
    }

    class RecipeCategoryBuilder {
        private final String name;
        private final List<RecipeGroupOptions> groups = new ArrayList<>();
        private RecipeGroupOptions search;

        protected RecipeCategoryBuilder(String name) {
            this.name = name.toUpperCase();
        }

        /**
         * Add a search to the category, this will automatically include all items.
         * @return this builder
         */
        public RecipeCategoryBuilder addSearch() {
            this.search = new RecipeGroupOptions(this.name + "_SEARCH", o -> false, "minecraft:compass");
            return this;
        }

        /**
         * Add a new group to the category
         * @param name a name for the group
         * @param recipeSelectionCriteria this is the criteria for items to be added to the group,
         *                                the lambda parameter will always be a {@link net.minecraft.recipe.Recipe} object,
         *                                we just can't reference it because this happens before the class is loaded.
         *                                This should return <code>true</code> if the recipe should belong to this category,
         *                                it should ALWAYS start by checking the type of recipe (probably though <code>instanceof</code>.
         * @param icons an array of item identifiers to use for the icon
         * @return this builder
         */
        public RecipeCategoryBuilder addGroup(String name, RecipeGroupSelectionCriteria recipeSelectionCriteria, String... icons) {
            this.groups.add(new RecipeGroupOptions(this.name + "_" + name.toUpperCase(), recipeSelectionCriteria, icons));
            return this;
        }

        /**
         * Builds the category
         * @return the category options
         */
        public RecipeCategoryOptions build() {
            if (this.groups.isEmpty()) {
                throw new IllegalStateException(String.format("No categories defined for category %s, at least one must be supplied.", this.name));
            }

            return new RecipeCategoryOptions(this.name, this.search, ImmutableList.copyOf(this.groups));
        }
    }

    record RecipeGroupOptions(String name, RecipeGroupSelectionCriteria recipeSelectionCriteria, String... icons) {
    }

    class RecipeCategoryOptions {
        private final String name;
        private final RecipeGroupOptions searchGroup;
        private final List<RecipeGroupOptions> groups;
        private final List<RecipeGroupOptions> allGroups;

        RecipeCategoryOptions(String name, RecipeGroupOptions searchGroup, List<RecipeGroupOptions> groups) {
            this.name = name;
            this.searchGroup = searchGroup;
            this.groups = groups;
            this.allGroups = this.searchGroup == null ? this.groups : Lists.asList(this.searchGroup, this.groups.toArray(RecipeGroupOptions[]::new));
        }

        public String name() {
            return this.name;
        }

        public RecipeGroupOptions searchGroup() {
            return this.searchGroup;
        }

        public List<RecipeGroupOptions> groups() {
            return this.groups;
        }

        public List<RecipeGroupOptions> allGroupsAndSearch() {
            return this.allGroups;
        }
    }

    interface RecipeGroupSelectionCriteria {
        boolean test(Object /* Recipe<?> */ recipe);
    }
}
