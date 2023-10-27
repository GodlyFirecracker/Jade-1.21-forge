package snownee.jade.api;

import java.util.List;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.ui.Direction2D;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElement.Align;
import snownee.jade.api.ui.IElementHelper;

/**
 * Tooltip that you can append text and other render-able stuffs to.
 *
 * @author Snownee
 */
@NonExtendable
public interface ITooltip {

	void clear();

	/**
	 * Returns tooltip's number of lines
	 */
	int size();

	default boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Add a text to a new line
	 */
	default void add(Component component) {
		add(component, null);
	}

	/**
	 * Add a tagged text to a new line
	 */
	default void add(Component component, ResourceLocation tag) {
		add(size(), component, tag);
	}

	default void add(int index, Component component) {
		add(index, component, null);
	}

	default void add(int index, Component component, ResourceLocation tag) {
		add(index, IElementHelper.get().text(component).tag(tag));
	}

	default void addAll(List<Component> components) {
		components.forEach(this::add);
	}

	/**
	 * Add a render-able element to a new line
	 */
	default void add(IElement element) {
		add(size(), element);
	}

	default void add(int index, List<IElement> elements) {
		boolean first = true;
		for (IElement element : elements) {
			if (first) {
				add(index, element);
			} else {
				append(index, element);
			}
			first = false;
		}
	}

	default void add(List<IElement> elements) {
		add(size(), elements);
	}

	void add(int index, IElement element);

	/**
	 * Append a text to the last line
	 * <p>
	 * IMPORTANT: DO NOT use this to concat texts
	 */
	default void append(Component component) {
		append(component, null);
	}

	/**
	 * Append a tagged text to the last line
	 * <p>
	 * IMPORTANT: DO NOT use this to concat texts
	 */
	default void append(Component component, ResourceLocation tag) {
		append(IElementHelper.get().text(component).tag(tag));
	}

	/**
	 * Append a render-able element to the last line
	 */
	default void append(IElement element) {
		append(size() - 1, element);
	}

	/**
	 * Append render-able elements to the last line
	 */
	default void append(int index, List<IElement> elements) {
		for (IElement element : elements) {
			append(index, element);
		}
	}

	void append(int index, IElement element);

	/**
	 * Clear all elements that are tagged with this tag
	 */
	void remove(ResourceLocation tag);

	@Deprecated
	IElementHelper getElementHelper();

	/**
	 * Get all elements that are tagged with this tag
	 */
	List<IElement> get(ResourceLocation tag);

	List<IElement> get(int index, Align align);

	String getMessage();

	void setLineMargin(int index, Direction2D side, int margin);
}
