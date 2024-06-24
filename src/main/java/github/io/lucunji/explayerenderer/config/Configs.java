package github.io.lucunji.explayerenderer.config;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import github.io.lucunji.explayerenderer.Main;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Configs {
    private static final ConfigClassHandler<Configs> HANDLER = ConfigClassHandler.createBuilder(Configs.class)
            .id(Identifier.of(Main.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve(Main.MOD_ID + "_yacl.json"))
                    .build())
            .build();

    public static Configs getInstance() {
        return HANDLER.instance();
    }

    public static Screen genGui(Screen parent) {
        YetAnotherConfigLib yacl = HANDLER.generateGui();
        OptionUtils.forEachOptions(yacl, option -> option.addListener((o, p) -> o.applyValue()));
        return yacl.generateScreen(parent);
    }

    public static boolean isConfigScreen(Screen screen) {
        return screen != null && screen.getTitle() != null && screen.getTitle().equals(Text.translatable("yacl3.config." + Main.MOD_ID + ":config.title"));
    }

    @SerialEntry
    @AutoGen(category = "general")
    @Boolean
    public boolean enabled = true;
    @SerialEntry
    @AutoGen(category = "general")
    @Boolean
    public boolean spectatorAutoSwitch = true;
    @SerialEntry
    @AutoGen(category = "general")
    @StringField
    public String playerName = "";

    @SerialEntry
    @AutoGen(category = "general")
    @DoubleSlider(min = -0.5, max = 1.5, step = 0.01)
    public double offsetX = 0.12;
    @SerialEntry
    @AutoGen(category = "general")
    @DoubleSlider(min = -0.5, max = 2.5, step = 0.01)
    public double offsetY = 1.5;
    @SerialEntry
    @AutoGen(category = "general")
    @DoubleSlider(min = -180, max = 180, step = 0.01)
    public double rotationX = 0;
    @SerialEntry
    @AutoGen(category = "general")
    @DoubleSlider(min = -180, max = 180, step = 0.01)
    public double rotationY = 0;
    @SerialEntry
    @AutoGen(category = "general")
    @DoubleSlider(min = -180, max = 180, step = 0.01)
    public double rotationZ = 0;
    @SerialEntry
    @AutoGen(category = "general")
    @DoubleSlider(min = 0, max = 2, step = 0.01)
    public double size = 0.5;
    @SerialEntry
    @AutoGen(category = "general")
    @Boolean
    public boolean mirrored = false;

    @SerialEntry
    @AutoGen(category = "postures")
    @EnumCycler
    public PoseOffsetMethod poseOffsetMethod = PoseOffsetMethod.AUTO;
    @SerialEntry
    @AutoGen(category = "postures")
    @DoubleSlider(min = -3, max = 3, step = 0.01)
    public double sneakOffsetY = -0.35;
    @SerialEntry
    @AutoGen(category = "postures")
    @DoubleSlider(min = -3, max = 3, step = 0.01)
    public double swimCrawlOffsetY = -1.22;
    @SerialEntry
    @AutoGen(category = "postures")
    @DoubleSlider(min = -3, max = 3, step = 0.01)
    public double elytraOffsetY = -1.22;

    @SerialEntry
    @AutoGen(category = "rotations")
    @DoubleSlider(min = -180, max = 180, step = 0.1)
    public double pitchMin = -20;
    @SerialEntry
    @AutoGen(category = "rotations")
    @DoubleSlider(min = -180, max = 180, step = 0.1)
    public double pitchMax = 20;
    @SerialEntry
    @AutoGen(category = "rotations")
    @DoubleSlider(min = -90, max = 90, step = 0.1)
    public double pitchOffset = 0;
    @SerialEntry
    @AutoGen(category = "rotations")
    @DoubleSlider(min = -180, max = 180, step = 0.1)
    public double headYawMin = -15;
    @SerialEntry
    @AutoGen(category = "rotations")
    @DoubleSlider(min = -180, max = 180, step = 0.1)
    public double headYawMax = -15;
    @SerialEntry
    @AutoGen(category = "rotations")
    @DoubleSlider(min = -180, max = 180, step = 0.1)
    public double bodyYawMin = 0;
    @SerialEntry
    @AutoGen(category = "rotations")
    @DoubleSlider(min = -180, max = 180, step = 0.1)
    public double bodyYawMax = 0;

    @SerialEntry
    @AutoGen(category = "details")
    @Boolean
    public boolean hurtFlash = true;
    @SerialEntry
    @AutoGen(category = "details")
    @Boolean
    public boolean swingHands = true;
    @SerialEntry
    @AutoGen(category = "rotations")
    @DoubleSlider(min = -180, max = 180, step = 0.1)
    public double lightDegree = 0;
    @SerialEntry
    @AutoGen(category = "details")
    @Boolean
    public boolean useWorldLight = true;
    @SerialEntry
    @AutoGen(category = "rotations")
    @IntSlider(min = 0, max = 15, step = 1)
    public int worldLightMin = 2;
    @SerialEntry
    @AutoGen(category = "details")
    @Boolean
    public boolean renderVehicle = true;

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
}
