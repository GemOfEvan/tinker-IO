package tinker_io.plugins.jei;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;

public class SmartOutputRecipeWrapper extends BlankRecipeWrapper{

	protected final List<ItemStack> cast;
	protected final List<FluidStack> inputFluid;
	protected List<ItemStack> output;
	protected boolean isBasin;

	private final CastingRecipe recipe;

	public SmartOutputRecipeWrapper(List<ItemStack> casts, CastingRecipe recipe, boolean isBasin) {
		this.cast = casts;
		this.recipe = recipe;
		this.inputFluid = ImmutableList.of(recipe.getFluid());
		this.output = ImmutableList.of(recipe.getResult());
		this.isBasin = isBasin;
	}

	public SmartOutputRecipeWrapper(CastingRecipe recipe, boolean isBasin){
		this.isBasin = isBasin;
		// cast is not required
		if(recipe.cast != null) {
			cast = recipe.cast.getInputs();
		}
		else {
			cast = ImmutableList.of();
		}
		this.inputFluid = ImmutableList.of(recipe.getFluid());
		this.recipe = recipe;
		// special treatment of oredict output recipies
		if(recipe.getResult() == null) {
			output = null;
		}
		else {
			output = ImmutableList.of(recipe.getResult());
		}
	}

	public boolean hasCast() {
		return recipe.cast != null;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, ImmutableList.of(cast));
	    ingredients.setInputs(FluidStack.class, inputFluid);
	    ingredients.setOutputs(ItemStack.class, hookGetOutputs());
	}

	/*@Override
	public List<ItemStack> getInputs() {
		if(cast == null) {
			return this.getInputs();
		}
		return cast;
	}*/

	public List<ItemStack> hookGetOutputs() {
		if(output == null) {
			if(recipe.getResult() == null) {
				return ImmutableList.of();
			}
			// we lazily evaluate the output in case the oredict wasn't there before
			output = ImmutableList.of(recipe.getResult());
		}
		return output;
	}

	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if(recipe.consumesCast()) {
			String s = I18n.format("gui.jei.casting.consume");
			int x = 55;
			x -= minecraft.fontRenderer.getStringWidth(s)/2;
			minecraft.fontRenderer.drawString(s, x, 40, 0xaa0000);
		}
	}

	/*@Override
	public List<FluidStack> getFluidInputs() {
		return inputFluid;
	}*/

}
