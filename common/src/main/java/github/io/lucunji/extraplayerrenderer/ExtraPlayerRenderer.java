/*
 *     Highly configurable paper doll mod.
 *     Copyright (C) 2024  LucunJi, And all  Contributors
 *
 *     This file is part of Extra Player Renderer.
 *
 *     Extra Player Renderer is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Extra Player Renderer is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Extra Player Renderer.  If not, see <https://www.gnu.org/licenses/>.
 */

package github.io.lucunji.extraplayerrenderer;

import com.mojang.blaze3d.platform.InputConstants;
import github.io.lucunji.extraplayerrenderer.config.Configs;
import github.io.lucunji.extraplayerrenderer.config.persistence.ConfigPersistence;
import github.io.lucunji.extraplayerrenderer.config.persistence.GsonConfigPersistence;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class ExtraPlayerRenderer {
    public static final String MOD_ID = "extraplayerrenderer";
    public static final String MOD_NAME = "Extra Player Renderer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final KeyMapping CONFIG_KEY = new KeyMapping(
            "key.%s.openMenu".formatted(MOD_ID),
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            "key.%s.category".formatted(MOD_ID));
    public static final Configs CONFIGS = new Configs();
    public static final ConfigPersistence CONFIG_PERSISTENCE = new GsonConfigPersistence(Path.of("config/" + MOD_ID + "_v3.json"));

    public static ResourceLocation withExtraPlayerRendererNamespace(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        // Write common init code here.
        CONFIG_PERSISTENCE.load(ExtraPlayerRenderer.CONFIGS.getOptions());
        ExtraPlayerRenderer.LOGGER.info("ExtraPlayerRenderer init{}", CONFIG_PERSISTENCE);
    }
}
