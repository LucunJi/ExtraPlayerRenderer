package github.io.lucunji.explayerenderer.mixin.yacl;

import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControllerWidget.class)
public interface ControllerWidgetAccessor {
    @Accessor(remap = false)
    YACLScreen getScreen();
}