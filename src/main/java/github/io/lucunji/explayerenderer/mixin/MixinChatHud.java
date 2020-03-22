package github.io.lucunji.explayerenderer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {
    @Shadow public abstract int getWidth();

    @Shadow public abstract double getChatScale();

    @Shadow public abstract boolean isChatFocused();

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I"))
    public int onDrawText(TextRenderer textRenderer, String text, float x, float y, int color) {
        if (isChatFocused()) {
            return textRenderer.drawWithShadow(text, x, y, color);
        } else {
            boolean reduced = false;
            int actualWidth = MathHelper.floor((double)this.getWidth() / this.getChatScale());
            while (textRenderer.getStringWidth(text) > actualWidth / 2 - 9) {
                reduced = true;
                text = text.substring(0, text.length() - 1);
            }
            if (reduced) text += "...";
            return textRenderer.drawWithShadow(text, x, y - client.window.getScaledHeight() / 2 + 18, color);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(IIIII)V", ordinal = 0))
    public void onFill(int x1, int y1, int x2, int y2, int color) {
        if (!isChatFocused()) {
            y1 -= (client.window.getScaledHeight() / 2 - 18);
            y2 -= (client.window.getScaledHeight() / 2 - 18);
            x2 >>= 1;
            color = (color & 0xFFFFFF) | ((color & 0xF0000000) >> 1);
        }
        DrawableHelper.fill(x1, y1, x2, y2, color);
    }
}
