package github.io.lucunji.explayerenderer.config;

import github.io.lucunji.explayerenderer.api.config.model.ConfigOption;
import github.io.lucunji.explayerenderer.api.config.model.SimpleNumericOption;
import github.io.lucunji.explayerenderer.api.config.model.SimpleOption;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static github.io.lucunji.explayerenderer.Main.id;

@SuppressWarnings("unused")
public class Configs {
    public static final Identifier GENERAL_CATEGORY = id("general");
    public static final Identifier POSTURES_CATEGORY = id("postures");
    public static final Identifier ROTATIONS_CATEGORY = id("rotations");
    public static final Identifier DETAILS_CATEGORY = id("details");
    public static final Identifier HIDDEN_CATEGORY = id("hidden");

    private final List<? extends ConfigOption<?>> options;

    public Configs() {
        this.options = Arrays.stream(this.getClass().getFields())
                .map(field -> {
                    try {return field.get(this);} catch (IllegalAccessException e) {throw new RuntimeException(e);}
                })
                .filter(val -> val instanceof ConfigOption<?>)
                .map(f -> (ConfigOption<?>) f)
                .toList();

        var unique = new HashSet<Pair<Identifier, Identifier>>();
        for (ConfigOption<?> option : this.options) {
            if (!unique.add(Pair.of(option.getCategory(), option.getId()))) {
                throw new IllegalStateException("Duplicated option id: " + option.getId() + " in category " + option.getCategory());
            }
        }
    }

    public List<? extends ConfigOption<?>> getOptions() {return this.options;}

    public final SimpleOption<Boolean> enabled = new SimpleOption<>(GENERAL_CATEGORY, id("enabled"), true);
    public final SimpleOption<Boolean> spectatorAutoSwitch = new SimpleOption<>(GENERAL_CATEGORY, id("spectator_auto_switch"), true);
    public final SimpleOption<String> playerName = new SimpleOption<>(GENERAL_CATEGORY, id("player_name"), "");

    public final SimpleNumericOption<Double> offsetX = new SimpleNumericOption<>(GENERAL_CATEGORY, id("offset_x"), 0.12, -0.5, 1.5);
    public final SimpleNumericOption<Double> offsetY = new SimpleNumericOption<>(GENERAL_CATEGORY, id("offset_y"), 1.5, -0.5, 2.5);
    public final SimpleNumericOption<Double> rotationX = new SimpleNumericOption<>(GENERAL_CATEGORY, id("rotation_x"), 0D, -180D, 180D);
    public final SimpleNumericOption<Double> rotationY = new SimpleNumericOption<>(GENERAL_CATEGORY, id("rotation_y"), 0D, -180D, 180D);
    public final SimpleNumericOption<Double> rotationZ = new SimpleNumericOption<>(GENERAL_CATEGORY, id("rotation_z"), 0D, -180D, 180D);
    public final SimpleNumericOption<Double> size = new SimpleNumericOption<>(GENERAL_CATEGORY, id("size"), 0.5, 0D, 2D);
    public final SimpleOption<Boolean> mirrored = new SimpleOption<>(GENERAL_CATEGORY, id("mirrored"), false);

    public final SimpleOption<PoseOffsetMethod> poseOffsetMethod = new SimpleOption<>(POSTURES_CATEGORY, id("pose_offset_method"), PoseOffsetMethod.AUTO);
    public final SimpleNumericOption<Double> sneakOffsetY = new SimpleNumericOption<>(POSTURES_CATEGORY, id("sneak_offset_y"), -0.35, -3D, 3D);
    public final SimpleNumericOption<Double> swimCrawlOffsetY = new SimpleNumericOption<>(POSTURES_CATEGORY, id("swim_crawl_offset_y"), -1.22, -3D, 3D);
    public final SimpleNumericOption<Double> elytraOffsetY = new SimpleNumericOption<>(POSTURES_CATEGORY, id("elytra_offset_y"), -1.22, -3D, 3D);

    public final SimpleNumericOption<Double> pitch = new SimpleNumericOption<>(ROTATIONS_CATEGORY, id("pitch"), 0D, -90D, 90D);
    public final SimpleNumericOption<Double> pitchRange = new SimpleNumericOption<>(ROTATIONS_CATEGORY, id("pitch_range"), 20D, 0D, 90D);
    public final SimpleNumericOption<Double> headYaw = new SimpleNumericOption<>(ROTATIONS_CATEGORY, id("head_yaw"), -15D, -180D, 180D);
    public final SimpleNumericOption<Double> headYawRange = new SimpleNumericOption<>(ROTATIONS_CATEGORY, id("head_yaw_range"), 0D, 0D, 180D);
    public final SimpleNumericOption<Double> bodyYaw = new SimpleNumericOption<>(ROTATIONS_CATEGORY, id("body_yaw"), 0D, -180D, 180D);
    public final SimpleNumericOption<Double> bodyYawRange = new SimpleNumericOption<>(ROTATIONS_CATEGORY, id("body_yaw_range"), 0D, 0D, 180D);

    public final SimpleOption<Boolean> hurtFlash = new SimpleOption<>(DETAILS_CATEGORY, id("hurt_flash"), true);
    public final SimpleOption<Boolean> swingHands = new SimpleOption<>(DETAILS_CATEGORY, id("swing_hands"), true);
    public final SimpleNumericOption<Double> lightDegree = new SimpleNumericOption<>(DETAILS_CATEGORY, id("light_degree"), 0D, -180D, 180D);
    public final SimpleOption<Boolean> useWorldLight = new SimpleOption<>(DETAILS_CATEGORY, id("use_world_light"), true);
    public final SimpleNumericOption<Integer> worldLightMin = new SimpleNumericOption<>(DETAILS_CATEGORY, id("world_light_min"), 2, 0, 15);
    public final SimpleOption<Boolean> renderVehicle = new SimpleOption<>(DETAILS_CATEGORY, id("render_vehicle"), true);
    public final SimpleOption<Boolean> hideUnderDebug = new SimpleOption<>(DETAILS_CATEGORY, id("hide_under_debug"), true);

    public final SimpleOption<Integer> lastConfigTabIdx = new SimpleOption<>(HIDDEN_CATEGORY, id("last_config_tab_idx"), 0);

    public enum PoseOffsetMethod {
        AUTO, MANUAL, FORCE_STANDING, DISABLED
    }
}
