package snownee.jade.addon.vanilla;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum CropProgressProvider implements IBlockComponentProvider {

	INSTANCE;

	@Override
	public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
		if (accessor.getBlock() == Blocks.WHEAT) {
			return IElementHelper.get().item(new ItemStack(Items.WHEAT));
		}

		if (accessor.getBlock() == Blocks.BEETROOTS) {
			return IElementHelper.get().item(new ItemStack(Items.BEETROOT));
		}

		return null;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		BlockState state = accessor.getBlockState();
		Block block = state.getBlock();

		if (block instanceof CropBlock crop) {
			addMaturityTooltip(tooltip, crop.getAge(state) / (float) crop.getMaxAge());
		} else if (state.hasProperty(BlockStateProperties.AGE_7)) {
			addMaturityTooltip(tooltip, state.getValue(BlockStateProperties.AGE_7) / 7F);
		} else if (state.hasProperty(BlockStateProperties.AGE_2)) {
			addMaturityTooltip(tooltip, state.getValue(BlockStateProperties.AGE_2) / 2F);
		} else if (state.hasProperty(BlockStateProperties.AGE_3)) {
			if (block instanceof SweetBerryBushBlock || block instanceof NetherWartBlock) {
				addMaturityTooltip(tooltip, state.getValue(BlockStateProperties.AGE_3) / 3F);
			}
		}
	}

	private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
		growthValue *= 100.0F;
		if (growthValue < 100.0F) {
			tooltip.add(Component.translatable("tooltip.jade.crop_growth", IThemeHelper.get().info(String.format("%.0f%%", growthValue))));
		} else {
			tooltip.add(Component.translatable(
					"tooltip.jade.crop_growth",
					IThemeHelper.get().success(Component.translatable("tooltip.jade.crop_mature"))));
		}
	}

	@Override
	public ResourceLocation getUid() {
		return Identifiers.MC_CROP_PROGRESS;
	}

}
