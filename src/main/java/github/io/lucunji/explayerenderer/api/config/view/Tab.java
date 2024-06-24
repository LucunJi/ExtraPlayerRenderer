package github.io.lucunji.explayerenderer.api.config.view;

import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A container that does not render but provides inner widgets to
 * {@link net.minecraft.client.gui.widget.TabNavigationWidget} instances
 */
public class Tab implements net.minecraft.client.gui.tab.Tab {
    private final Text title;
    private final List<ClickableWidget> children;

    public Tab(Text title) {
        this.title = title;
        this.children = new ArrayList<>();
    }

    public void addChild(ClickableWidget child) {
        this.children.add(child);
    }

    @Override
    public Text getTitle() {
        return this.title;
    }

    /**
     * Used to load/unload children when switching tab
     */
    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        children.forEach(consumer);
    }

    /**
     * Seems useless here
     */
    @Override
    public void refreshGrid(ScreenRect tabArea) {}
}
