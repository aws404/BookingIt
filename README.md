## Booking It 
A library mod to scoot around the hardcoded and enum filled mess that is the recipe book! Add categories and groups with ease.

## For Mod Developers
### Dependencies
You can include the mod using the following repository and dependency in your `build.gradle` (where `[TAG]` is the latest version):
```gradle
repositories {
  maven {
    name = "Modrinth"
    url = "https://api.modrinth.com/maven"
    content {
      includeGroup "maven.modrinth"
    }
  }
}

dependencies {
  modImplementation include("maven.modrinth:booking_it:[TAG]") //To include (jij) in your mod
  modImplementation "maven.modrinth:booking_it:[TAG]" // To require the mod to be installed seperatly
}
```

### Adding A Category
The recipe book categories are hardcoded enums, because of this we need to inject our categories before the main game is loaded.
To do this, create an entrypoint in your fabric.mod.json called `booking_it:recipe_book` and point it to a class that implements `RecipeBookAdder`. While you're in the file, you should also add the `booking_it` dependency (`*` should do for now).
Fo example:
```json
{
  "entrypoints": {
    "booking_it:recipe_book": [
      "com.github.aws404.booking_it_example.TestModRecipeBookAdder"
    ]
  },
  "depends": {
    "booking_it": "*"
  }
}
```

Now for some quick definitions:
* Category - A recipe book category is most similar to the recipe type, for example, vanilla's `CRAFTING` category contains all recipes made in the crafting table.
* Group - A recipe group are the tabs along the side of a category's book. (Note that groups are purely visual and do not exist on the server side).

A vanilla example:
<table>
    <tbody>
        <tr>
            <th>Category</th>
            <td colspan=3 align="center">BLAST_FURNACE</td>
        </tr>
        <tr>
            <th>Groups</th>
            <td>BLAST_FURNACE_SEARCH</td>
            <td>BLAST_FURNACE_BLOCKS</td>
            <td>BLAST_FURNACE_MISC</td>
        </tr>
    </tbody>
</table>

Now that's out of the way we can start building our categories and their groups.
First, in your `RecipeBookAdder` class, you need to implement to `getCategories` method, which is the method that will return a list of your new categories in the form of `RecipeCategoryOptions`.
We will build the `RecipeCategoryOptions` using a `RecipeCategoryBuilder` obtained by calling `RecipeBookAdder#builder(String cateogryName)`.
Here's where we are:
```java
public class TestModRecipeBookAdder implements RecipeBookAdder {
    @Override
    public List<RecipeCategoryOptions> getCategories() {
        return List.of(
                RecipeBookAdder.builder("COOKING")
                        .build()
        );
    }
}
```
But! this will not work until we add a group. We do this by calling `RecipeCategoryBuilder.addGroup(String name, RecipeGroupSelectionCriteria recipeSelectionCriteria, String... icons)`, where:
* `name` is the name of our category. Note that for the actual enum name, the group name will be appended to category name. For example, specifying the group `ALL` will use the full name `COOKING_ALL` to avoid conflict.
* `recipeSelectionCriteria` is the criteria for a recipe to be added to this group. It will always be an instance of `Recipe<?>`, but you need to check for your specific recipe type (through an `instanceof` check).
* `icons` is a list of item identifiers to use as the icons on the groups tab.

With that in mind, lets add a `BLOCKS` category, for items in a select creative tab, and a misc for everything else. (Recipes can also appear in multiple categories if the criteria allows)
```java
public class TestModRecipeBookAdder implements RecipeBookAdder {
    @Override
    public List<RecipeCategoryOptions> getCategories() {
        return List.of(
                RecipeBookAdder.builder("COOKING")
                        .addGroup("BLOCKS", recipe -> {
                            if (recipe instanceof CookingRecipe cookingRecipe) {
                                return cookingRecipe.getOutput().getItem().getGroup() == ItemGroup.BUILDING_BLOCKS;
                            }
                            return false;
                        }, "minecraft:cobblestone", "minecraft:dirt")
                        .addGroup("MISC", recipe -> {
                            if (recipe instanceof CookingRecipe cookingRecipe) {
                                return cookingRecipe.getOutput().getItem().getGroup() != ItemGroup.BUILDING_BLOCKS;
                            }
                            return false;
                        }, "minecraft:stick", "minecraft:flint")
                        .build()
        );
    }
}
```
Great, now we can even choose to add a search group which will contain items from every other group.
```java
public class TestModRecipeBookAdder implements RecipeBookAdder {
    @Override
    public List<RecipeCategoryOptions> getCategories() {
        return List.of(
                RecipeBookAdder.builder("COOKING")
                        .addSearch()
                        .addGroup("BLOCKS", recipe -> {
                            if (recipe instanceof CookingRecipe cookingRecipe) {
                                return cookingRecipe.getOutput().getItem().getGroup() == ItemGroup.BUILDING_BLOCKS;
                            }
                            return false;
                        }, "minecraft:cobblestone", "minecraft:dirt")
                        .addGroup("MISC", recipe -> {
                            if (recipe instanceof CookingRecipe cookingRecipe) {
                                return cookingRecipe.getOutput().getItem().getGroup() != ItemGroup.BUILDING_BLOCKS;
                            }
                            return false;
                        }, "minecraft:stick", "minecraft:flint")
                        .build()
        );
    }
}
```