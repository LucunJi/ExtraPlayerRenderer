package github.io.lucunji.explayerenderer.mixin.yacl.patch;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.config.OptionPatch;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.isxander.yacl3.gui.YACLScreen.CategoryTab;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = YACLScreen.class, priority = Integer.MIN_VALUE)
public abstract class YACLScreenMixin extends Screen {
    @Shadow
    @Final
    public YetAnotherConfigLib config;
    @Shadow public Text saveButtonMessage;

    /**
     * pendingChanges should be renamed to isDirty
     */
    @Shadow
    public abstract boolean pendingChanges();

    @Shadow private boolean pendingChanges;
    @Shadow
    @Final
    public TabManager tabManager;


    protected YACLScreenMixin(Text title) {super(title);}

    @Inject(method = "undo", at = @At("HEAD"), remap = false, cancellable = true)
    public void onUndo(CallbackInfo ci) {
        if (!Configs.isConfigScreen(MinecraftClient.getInstance().currentScreen)) return;
        ci.cancel();
        OptionUtils.forEachOptions(config, option -> ((OptionPatch<?>) option).extraPlayerRenderer$restoreSavedValue());
    }

    @Inject(method = "cancelOrReset", at = @At("HEAD"), remap = false, cancellable = true)
    public void onCancelOrReset(CallbackInfo ci) {
        if (!Configs.isConfigScreen(MinecraftClient.getInstance().currentScreen)) return;
        ci.cancel();
        if (pendingChanges()) { // if pending changes, button acts as a cancel button
            OptionUtils.forEachOptions(config, option -> ((OptionPatch<?>) option).extraPlayerRenderer$restoreSavedValue());
            close();
        } else { // if not, button acts as a reset button
            OptionUtils.forEachOptions(config, Option::requestSetDefault);
        }
    }

    @Inject(method = "finishOrSave", at = @At("HEAD"), remap = false, cancellable = true)
    public void onFinishOrSave(CallbackInfo ci) {
        if (!Configs.isConfigScreen(MinecraftClient.getInstance().currentScreen)) return;
        ci.cancel();
        saveButtonMessage = null;

        if (pendingChanges()) {
            Set<OptionFlag> flags = new HashSet<>();
            OptionUtils.forEachOptions(config, option -> {
                if (((OptionPatch<?>) option).extraPlayerRenderer$savePendingValue()) {
                    flags.addAll(option.flags());
                }
            });
            OptionUtils.forEachOptions(config, option -> {
                if (option.changed()) {
                    // if still changed after applying, reset to the current value from binding
                    // as something has gone wrong.
                    ((OptionPatch<?>) option).extraPlayerRenderer$restoreSavedValue();
                    YACLConstants.LOGGER.error("Option '{}' value mismatch after applying! Reset to saved value.", option.name().getString());
                }
            });
            config.saveFunction().run();

            flags.forEach(flag -> flag.accept(client));

            pendingChanges = false;
            if (tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
                categoryTab.updateButtons();
            }
        } else close();
    }
}
