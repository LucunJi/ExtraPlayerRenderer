package github.io.lucunji.explayerenderer.config;

import dev.isxander.yacl3.api.NameableEnum;
import github.io.lucunji.explayerenderer.Main;
import net.minecraft.text.Text;

public enum PoseOffsetMethod implements NameableEnum {
    AUTO, MANUAL, FORCE_STANDING, DISABLED;

    public final String nameKey;

    PoseOffsetMethod() {
        this.nameKey = "yacl3.config." + Main.MOD_ID + ":config.poseOffsetMethod." + this.name();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(this.nameKey);
    }
}
