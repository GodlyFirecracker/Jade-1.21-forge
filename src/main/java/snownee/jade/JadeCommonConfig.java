package snownee.jade;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import snownee.jade.addon.forge.InventoryProvider;

public final class JadeCommonConfig {

	public static int inventoryDetailedShowAmount = 54;
	public static int inventoryNormalShowAmount = 9;
	public static int inventoryShowItemPreLine = 9;
	private static final Set<BlockEntityType<?>> inventoryBlacklist = Sets.newHashSet();
	public static boolean bypassLockedContainer = false;
	private static boolean onlyShowVanilla = false;
	private static final Set<String> modBlacklist = Sets.newHashSet();

	private static IntValue inventorySneakShowAmountVal;
	private static IntValue inventoryNormalShowAmountVal;
	private static IntValue inventoryShowItemPreLineVal;
	private static ConfigValue<List<? extends String>> inventoryBlacklistVal;
	private static BooleanValue bypassLockedContainerVal;
	private static BooleanValue onlyShowVanillaVal;
	private static ConfigValue<List<? extends String>> modBlacklistVal;

	static final ForgeConfigSpec spec = new ForgeConfigSpec.Builder().configure(JadeCommonConfig::new).getRight();

	private JadeCommonConfig(ForgeConfigSpec.Builder builder) {
		builder.push("inventory");
		inventorySneakShowAmountVal = builder.defineInRange("sneakShowAmount", inventoryDetailedShowAmount, 0, 54);
		inventoryNormalShowAmountVal = builder.defineInRange("normalShowAmount", inventoryNormalShowAmount, 0, 54);
		inventoryShowItemPreLineVal = builder.defineInRange("showItemPreLine", inventoryShowItemPreLine, 1, 18);
		inventoryBlacklistVal = builder.defineList("blacklist", () -> Collections.singletonList("refinedstorage:disk_drive"), Predicates.alwaysTrue());
		bypassLockedContainerVal = builder.define("bypassLockedContainer", bypassLockedContainer);
		builder.pop();
		builder.push("customContainerName");
		onlyShowVanillaVal = builder.define("onlyShowVanilla", onlyShowVanilla);
		modBlacklistVal = builder.defineList("blacklist", () -> Collections.singletonList("thermal"), Predicates.alwaysTrue());
	}

	public static void refresh() {
		inventoryDetailedShowAmount = inventorySneakShowAmountVal.get();
		inventoryNormalShowAmount = inventoryNormalShowAmountVal.get();
		inventoryShowItemPreLine = inventoryShowItemPreLineVal.get();
		bypassLockedContainer = bypassLockedContainerVal.get();
		inventoryBlacklist.clear();
		inventoryBlacklist.addAll(parseBlockEntityTypes(InventoryProvider.INVENTORY_IGNORE));
		inventoryBlacklist.addAll(parseBlockEntityTypes(inventoryBlacklistVal.get()));

		onlyShowVanilla = onlyShowVanillaVal.get();
		modBlacklist.clear();
		modBlacklist.addAll(modBlacklistVal.get());
	}

	@SubscribeEvent
	public static void onConfigReload(ModConfigEvent.Reloading event) {
		((CommentedFileConfig) event.getConfig().getConfigData()).load();
		refresh();
	}

	public static boolean shouldIgnoreTE(BlockEntityType<?> type) {
		return inventoryBlacklist.contains(type);
	}

	public static boolean shouldIgnoreTE(String id) {
		return shouldIgnoreTE(Registry.BLOCK_ENTITY_TYPE.get(ResourceLocation.tryParse(id)));
	}

	private static List<BlockEntityType<?>> parseBlockEntityTypes(Collection<? extends String> ids) {
		try {
			return (List<BlockEntityType<?>>) (Object) ids.stream()
					.map(ResourceLocation::tryParse)
					.map(Registry.BLOCK_ENTITY_TYPE::get)
					.filter(Objects::nonNull)
					.toList();
		} catch (Exception e) {
			return List.of();
		}
	}

	public static boolean shouldShowCustomName(BlockEntity t) {
		String modid = t.getType().getRegistryName().getNamespace();
		if (onlyShowVanilla) {
			return "minecraft".equals(modid);
		} else {
			return !modBlacklist.contains(modid);
		}
	}

}
