package snownee.jade.api.fluid;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;

public class JadeFluidObject {

	public static long bucketVolume() {
		return FluidType.BUCKET_VOLUME;
	}

	public static long blockVolume() {
		return FluidType.BUCKET_VOLUME;
	}

	public static JadeFluidObject empty() {
		return of(Fluids.EMPTY, 0);
	}

	public static JadeFluidObject of(Fluid fluid) {
		return of(fluid, blockVolume());
	}

	public static JadeFluidObject of(Fluid fluid, long amount) {
		return of(fluid, amount, null);
	}

	public static JadeFluidObject of(Fluid fluid, long amount, CompoundTag tag) {
		return new JadeFluidObject(fluid, amount, tag);
	}

	private final Fluid type;
	private final long amount;
	@Nullable
	private final CompoundTag tag;

	private JadeFluidObject(Fluid type, long amount, @Nullable CompoundTag tag) {
		this.type = type;
		this.amount = amount;
		this.tag = tag;
		Objects.requireNonNull(type);
	}

	public Fluid getType() {
		return type;
	}

	public long getAmount() {
		return amount;
	}

	public @Nullable CompoundTag getTag() {
		return tag;
	}

	public boolean isEmpty() {
		return getType() == Fluids.EMPTY || getAmount() == 0;
	}
}
