package github.io.lucunji.explayerenderer.mixin;

import github.io.lucunji.explayerenderer.client.render.PlayerHUDRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static github.io.lucunji.explayerenderer.Main.CONFIGS;

/**
 * <h2>
 * Order of rendering HUDs (as of 1.21):
 * </h2>
 * <br/>
 * ({@link net.minecraft.client.option.GameOptions#hudHidden} controls all but {@link InGameHud#renderSleepOverlay})
 * <br/>
 * <ul>
 *     <li>{@link InGameHud#renderMiscOverlays}</li>
 *     <ul>
 *         <li>spyglass</li>
 *         <li>pumpkin</li>
 *         <li>powdered snow</li>
 *         <li>portal</li>
 *     </ul>
 *     <li>{@link InGameHud#renderCrosshair}</li>
 *     <li>{@link InGameHud#renderMainHud}</li>
 *     <ul>
 *         <li>spectator menu / hotbar</li>
 *         <li>mount jumping bar / experience bar</li>
 *         <li>mount health</li>
 *         <li>handheld item tooltip / spectator menu tooltip</li>
 *     </ul>
 *     <li>{@link InGameHud#renderExperienceLevel} (the text)</li>
 *     <li>{@link InGameHud#renderStatusEffectOverlay}</li>
 *     <li>{@link InGameHud#bossBarHud}</li>
 *     <li>{@link InGameHud#renderSleepOverlay}</li>
 *     <li>{@link InGameHud#renderDemoTimer}</li>
 *     <li>{@link InGameHud#debugHud}</li>
 *     <li>{@link InGameHud#renderScoreboardSidebar}</li>
 *     <li>{@link InGameHud#renderOverlayMessage}</li>
 *     <li>{@link InGameHud#renderTitleAndSubtitle} (message given by /title)</li>
 *     <li>{@link InGameHud#renderChat}</li>
 *     <li>{@link InGameHud#renderPlayerList}</li>
 *     <li>{@link InGameHud#subtitlesHud} (sound subtitles)</li>
 * </ul>
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Final
    @Shadow
    private MinecraftClient client;
    @Unique
    private PlayerHUDRenderer playerHUDRenderer;

    /**
     * Initialization should go after initialization to prevent using uninitialized fields.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(MinecraftClient client, CallbackInfo ci) {
        this.playerHUDRenderer = new PlayerHUDRenderer(this.client);
    }

    /**
     * Should render when:
     * <ul>
     *     <li>Hud is NOT hidden (F1),</li>
     *     <li>and NOT showing debug HUD (F3) or the corresponding option is turned off,</li>
     *     <li>game is not showing any screen</li>
     * </ul>
     * <p>
     * By injecting into one of the stages,
     * we hook ourselves into {@link InGameHud#layeredDrawer},
     * and need to obey the rules of {@link net.minecraft.client.gui.LayeredDrawer} (especially in {@link net.minecraft.client.gui.LayeredDrawer#renderInternal})
     * to act like a layer of HUD.
     */
    @Inject(method = "renderMiscOverlays", at = @At("RETURN"))
    void onRenderMiscOverlayFinish(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!this.client.options.hudHidden
                && !(CONFIGS.hideUnderDebug.getValue() && this.client.getDebugHud().shouldShowDebugHud())
                && this.client.currentScreen == null) {
            this.playerHUDRenderer.render(tickCounter.getTickDelta(true));
        }
        // follow convention in LayeredDrawer#renderInternal
        context.getMatrices().translate(0, 0, 200);
    }
}
