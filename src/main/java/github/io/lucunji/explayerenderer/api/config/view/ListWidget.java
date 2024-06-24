package github.io.lucunji.explayerenderer.api.config.view;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.Identifier;

import static github.io.lucunji.explayerenderer.Main.id;

public class ListWidget extends ElementListWidget<ListWidget.ListEntry> implements Retextured {

    private int rowWidth;

    public ListWidget(int width, int height, int top, int entryHeight) {
        super(MinecraftClient.getInstance(), width, height, top, entryHeight);
        this.rowWidth = super.getRowWidth();
    }

    @Override
    public int addEntry(ListEntry entry) {return super.addEntry(entry);}

    public void setRowWidth(int rowWidth) {this.rowWidth = rowWidth;}

    @Override
    public int getRowWidth() {return this.rowWidth;}

    @Override
    public Identifier retexture(Identifier oldTexture) {
        return id(oldTexture.getPath());
    }

    public static abstract class ListEntry extends ElementListWidget.Entry<ListEntry> {}
}
