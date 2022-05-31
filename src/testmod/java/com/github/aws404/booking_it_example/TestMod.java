package com.github.aws404.booking_it_example;

import com.github.aws404.booking_it_example.cooking.*;
import com.github.aws404.booking_it_example.one_to_one_crafting.OneToOneCraftingScreen;
import com.github.aws404.booking_it_example.one_to_one_crafting.OneToOneCraftingScreenHandler;
import com.github.aws404.booking_it_example.one_to_one_crafting.OneToOneRecipe;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import com.github.aws404.booking_it.BookingIt;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMod implements ModInitializer, ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Booking It Testmod");

	public static final RecipeType<CookingRecipe> TEST_RECIPE_TYPE = RecipeType.register("bi_test:cooking");
	public static final RecipeSerializer<CookingRecipe> TEST_RECIPE_SERIALISER = RecipeSerializer.register("bi_test:cooking", new CookingRecipeSerializer<>(CookingRecipe::new, 100));

	public static final RecipeType<OneToOneRecipe> ONE_TO_ONE_RECIPE_TYPE = RecipeType.register("bi_test:one_to_one");
	public static final RecipeSerializer<OneToOneRecipe> ONE_TO_ONE_RECIPE_SERIALISER = RecipeSerializer.register("bi_test:one_to_one", new OneToOneRecipe.Serialiser());

	public static final Block TEST_COOKING_BLOCK = Registry.register(Registry.BLOCK, "bi_test:cooking", new CookingBlock(FabricBlockSettings.of(Material.METAL)));
	public static final BlockEntityType<CookingBlockEntity> TEST_COOKING_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "bi_test:cooking", FabricBlockEntityTypeBuilder.create(CookingBlockEntity::new, TEST_COOKING_BLOCK).build());

	public static final ScreenHandlerType<CookingScreenHandler> TEST_COOKING_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier("bi_test", "cooking"), CookingScreenHandler::new);
	public static final ScreenHandlerType<OneToOneCraftingScreenHandler> ONE_TO_ONE_CRAFTING_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier("bi_test", "one_to_one"), OneToOneCraftingScreenHandler::new);

	// If you need to access the CATEGORY, you can use these to methods on BOTH the client and the server
	public static final RecipeBookCategory COOKING_BOOK_CATEGORY = BookingIt.getCategory("COOKING");
	public static final RecipeBookCategory ONE_TO_ONE_BOOK_CATEGORY = BookingIt.getCategory("ONE_TO_ONE");

	@Override
	public void onInitializeClient() {
		// Recipe book GROUPS are only used by the CLIENT, if you reference them make sure it's only on the client! (or you will crash!)
		RecipeBookGroup everythingOneToOneGroup = BookingIt.getGroup(ONE_TO_ONE_BOOK_CATEGORY, "ALL");
		LOGGER.info("Everything Category Icons: {}", everythingOneToOneGroup.getIcons());

		ScreenRegistry.register(TEST_COOKING_SCREEN_HANDLER_TYPE, CookingScreen::new);
		ScreenRegistry.register(ONE_TO_ONE_CRAFTING_SCREEN_HANDLER_TYPE, OneToOneCraftingScreen::new);
	}

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, registrationEnvironment) -> {
			dispatcher.register(CommandManager.literal("open_crafting")
					.executes(context -> {
						context.getSource().getPlayer().openHandledScreen(new NamedScreenHandlerFactory() {
							@Override
							public Text getDisplayName() {
								return Text.translatable("container.one_to_one");
							}

							@Override
							public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
								return new OneToOneCraftingScreenHandler(syncId, inv);
							}
						});
						return 1;
					})
			);
		});
	}
}
