package snownee.jade.api.view;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.Accessor;
import snownee.jade.util.CommonProxy;

public class ItemView {

	public ItemStack item;
	@Nullable
	public String text;

	public ItemView(ItemStack item) {
		this(item, null);
	}

	public ItemView(ItemStack item, @Nullable String text) {
		this.item = item;
		this.text = text;
	}

	@Deprecated
	public static ViewGroup<ItemStack> fromContainer(Container container, int maxSize, int startIndex) {
		return compacted(IntStream.range(startIndex, container.getContainerSize()).limit(maxSize * 3).mapToObj(container::getItem), maxSize);
	}

	@Deprecated
	public static ViewGroup<ItemStack> compacted(Stream<ItemStack> stream, int maxSize) {
		List<ItemStack> stacks = Lists.newArrayList();
		MutableInt start = new MutableInt();
		/* off */
		stream
				.filter(stack -> !stack.isEmpty())
				.filter(stack -> {
					if (stack.hasTag() && stack.getTag().contains("CustomModelData")) {
						for (String key : stack.getTag().getAllKeys()) {
							if (key.toLowerCase(Locale.ENGLISH).endsWith("clear") && stack.getTag().getBoolean(key)) {
								return false;
							}
						}
					}
					return true;
				})
				.forEach(stack -> {
					int size = stacks.size();
					if (size > maxSize)
						return;
					for (int i = 0; i < size; i++) {
						int j = (i + start.intValue()) % size;
						if (ItemStack.isSameItemSameTags(stack, stacks.get(j))) {
							stacks.get(j).grow(stack.getCount());
							start.setValue(j);
							return;
						}
					}
					stacks.add(stack.copy());
				});
		/* on */
		if (stacks.size() > maxSize)
			stacks.remove(maxSize);
		return new ViewGroup<>(stacks);
	}

	public static List<ViewGroup<ItemStack>> groupOf(Container container, Accessor<?> accessor) {
		return CommonProxy.containerGroup(container, accessor);
	}

	/**
	 * @param storage  On Fabric, it accepts {@code Storage<ItemVariant>}. On Forge/NeoForge, it accepts {@code IItemHandler}.
	 * @param accessor
	 */
	public static List<ViewGroup<ItemStack>> groupOf(Object storage, Accessor<?> accessor) {
		return CommonProxy.storageGroup(storage, accessor);
	}

}
