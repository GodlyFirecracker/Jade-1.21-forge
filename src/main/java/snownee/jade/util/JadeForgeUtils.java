package snownee.jade.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.google.common.math.IntMath;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import snownee.jade.addon.universal.ItemIterator;
import snownee.jade.api.view.ViewGroup;

public class JadeForgeUtils {

	private JadeForgeUtils() {
	}

	public static ItemIterator<? extends IItemHandler> fromItemHandler(IItemHandler storage, int fromIndex) {
		return fromItemHandler(storage, fromIndex, target -> {
			if (target instanceof CapabilityProvider<?> capProvider) {
				return capProvider.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
			}
			return null;
		});
	}

	public static ItemIterator<? extends IItemHandler> fromItemHandler(IItemHandler storage, int fromIndex, Function<Object, @Nullable IItemHandler> containerFinder) {
		return new ItemIterator.SlottedItemIterator<>(containerFinder, fromIndex) {

			@Override
			protected int getSlotCount(IItemHandler container) {
				return container.getSlots();
			}

			@Override
			protected ItemStack getItemInSlot(IItemHandler container, int slot) {
				return container.getStackInSlot(slot);
			}
		};
	}

	public static CompoundTag fromFluidStack(FluidStack fluidStack, long capacity) {
		CompoundTag tag = new CompoundTag();
		if (capacity <= 0) {
			return tag;
		}
		tag.putString("fluid", BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).toString());
		tag.putLong("amount", fluidStack.getAmount());
		tag.putLong("capacity", capacity);
		if (fluidStack.getTag() != null) {
			tag.put("tag", fluidStack.getTag());
		}
		return tag;
	}

	public static List<ViewGroup<CompoundTag>> fromFluidHandler(IFluidHandler fluidHandler) {
		List<CompoundTag> list = new ArrayList<>(fluidHandler.getTanks());
		int emptyCapacity = 0;
		for (int i = 0; i < fluidHandler.getTanks(); i++) {
			int capacity = fluidHandler.getTankCapacity(i);
			if (capacity <= 0)
				continue;
			FluidStack fluidStack = fluidHandler.getFluidInTank(i);
			if (fluidStack.isEmpty()) {
				emptyCapacity = IntMath.saturatedAdd(emptyCapacity, capacity);
				continue;
			}
			list.add(fromFluidStack(fluidStack, capacity));
		}
		if (list.isEmpty() && emptyCapacity > 0) {
			list.add(fromFluidStack(FluidStack.EMPTY, emptyCapacity));
		}
		if (!list.isEmpty()) {
			return List.of(new ViewGroup<>(list));
		}
		return List.of();
	}
}
