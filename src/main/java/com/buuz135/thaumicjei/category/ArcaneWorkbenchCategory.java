/*
 * This file is part of Hot or Not.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.thaumicjei.category;

import com.buuz135.thaumicjei.ThaumicJEI;
import com.buuz135.thaumicjei.drawable.AlphaDrawable;
import com.buuz135.thaumicjei.drawable.ItemStackDrawable;
import com.buuz135.thaumicjei.util.ResearchUtils;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArcaneWorkbenchCategory implements IRecipeCategory<ArcaneWorkbenchCategory.ArcaneWorkbenchWrapper> {

    public static final String UUID = "THAUMCRAFT_ARCANE_WORKBENCH";

    private IGuiHelper helper;

    public ArcaneWorkbenchCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public String getUid() {
        return UUID;
    }

    @Override
    public String getTitle() {
        return new ItemStack(Block.getBlockFromName(new ResourceLocation("thaumcraft", "arcane_workbench").toString())).getDisplayName();
    }

    @Override
    public String getModName() {
        return ThaumicJEI.MOD_NAME;
    }

    @Override
    public IDrawable getBackground() {
        return new AlphaDrawable(new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook_overlay.png"), 225, 31, 102, 102, 36, 0, 30, 30);
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return new ItemStackDrawable(new ItemStack(Block.getBlockFromName(new ResourceLocation("thaumcraft", "arcane_workbench").toString())));
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook_overlay.png"));
        GL11.glEnable(3042);
        Gui.drawModalRectWithCustomSizedTexture(51 - 16 + 30, 0, 40, 6, 32, 32, 512, 512);
        Gui.drawModalRectWithCustomSizedTexture(0 - 18 + 30, 4, 135, 152, 23, 23, 512, 512);
        GL11.glDisable(3042);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ArcaneWorkbenchWrapper recipeWrapper, IIngredients ingredients) {
        if (recipeWrapper.getRecipe() instanceof ShapelessArcaneRecipe) {
            recipeLayout.setShapeless();
        }
        recipeLayout.getItemStacks().init(0, false, 51 - 9 + 30, 7);
        int sizeX = 3;
        int sizeY = 3;
        int slot = 1;
        for (int y = 0; y < sizeY; ++y) {
            for (int x = 0; x < sizeX; ++x) {
                if (ingredients.getInputs(VanillaTypes.ITEM).size() >= slot) {
                    recipeLayout.getItemStacks().init(slot, true, 12 + (x * 30) + 30, 36 + 12 + (y) * 30);
                    if (ingredients.getInputs(VanillaTypes.ITEM).get(slot - 1) != null) {
                        recipeLayout.getItemStacks().set(slot, ingredients.getInputs(VanillaTypes.ITEM).get(slot - 1));
                    }
                    ++slot;
                }
            }
        }
        int crystalAmount = 0;
        if (recipeWrapper.getRecipe().getCrystals() != null) {
            for (Aspect aspect : recipeWrapper.getRecipe().getCrystals().getAspectsSortedByAmount()) {
                ItemStack crystal = new ItemStack(ItemsTC.crystalEssence);
                ((ItemCrystalEssence) ItemsTC.crystalEssence).setAspects(crystal, new AspectList().add(aspect, 1));
                crystal.setCount(recipeWrapper.getRecipe().getCrystals().getAmount(aspect));
                recipeLayout.getItemStacks().init(slot + crystalAmount, false, 118 + 23, 6 + 22 * crystalAmount);
                recipeLayout.getItemStacks().set(slot + crystalAmount, crystal);
                ++crystalAmount;
            }
        }

        recipeLayout.getItemStacks().set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Arrays.asList();
    }

    public static class ArcaneWorkbenchWrapper implements IHasResearch, IRecipeWrapper {

        private final IArcaneRecipe recipe;

        public ArcaneWorkbenchWrapper(IArcaneRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> lists = new ArrayList<>();
            List<Ingredient> input = new ArrayList<>();
            ItemStack output = null;
            if (recipe instanceof ShapelessArcaneRecipe) {
                input = recipe.getIngredients();
                output = recipe.getRecipeOutput();
            } else if (recipe instanceof ShapedArcaneRecipe){
                ShapedArcaneRecipe shapedArcaneRecipe = (ShapedArcaneRecipe) recipe;
                int sizeX = shapedArcaneRecipe.getRecipeWidth();
                int sizeY = shapedArcaneRecipe.getRecipeHeight();
                int slot = 0;
                for (int y = 0; y < 3; ++y) {
                    for (int x = 0; x < 3; ++x) {
                        if (y < sizeY && x < sizeX){
                            input.add(recipe.getIngredients().get(slot));
                            ++slot;
                        } else {
                            input.add(Ingredient.EMPTY);
                        }
                    }
                }
                output = recipe.getRecipeOutput();
            }
            for (Ingredient ingredient : input) {
                lists.add(Arrays.asList(ingredient.getMatchingStacks()));
            }
            ingredients.setInputLists(VanillaTypes.ITEM, lists);
            ingredients.setOutput(VanillaTypes.ITEM, output);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            minecraft.fontRenderer.drawString(TextFormatting.DARK_GRAY + String.valueOf(recipe.getVis()), 50 - minecraft.fontRenderer.getStringWidth(String.valueOf(recipe.getVis())) / 2, 12, 0);
            if (!ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, getResearch()))
                minecraft.getRenderItem().renderItemIntoGUI(new ItemStack(Blocks.BARRIER), 15, 8);
        }

        @Nullable
        @Override
        public List<String> getTooltipStrings(int mouseX, int mouseY) {
            if (mouseX > 34 && mouseX < 60 && mouseY > 4 && mouseY < 28) {
                return Arrays.asList("Vis Cost");
            }
            if (!ThaumcraftCapabilities.knowsResearch(Minecraft.getMinecraft().player, getResearch()) && mouseX > 10 && mouseX < 34 && mouseY > 4 && mouseY < 28) {
                return ResearchUtils.generateMissingResearchList(getResearch());
            }
            return null;
        }

        @Override
        public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
            return false;
        }

        public IArcaneRecipe getRecipe() {
            return recipe;
        }

        @Override
        public String[] getResearch() {
            return new String[]{recipe.getResearch()};
        }
    }
}
