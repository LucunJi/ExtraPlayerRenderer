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

package github.io.lucunji.extraplayerrenderer.fabric;

import github.io.lucunji.extraplayerrenderer.CommonInterfaceInstances;
import github.io.lucunji.extraplayerrenderer.ExtraPlayerRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class ExtraPlayerRendererFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CommonInterfaceInstances.keyHelper = KeyBindingHelper::getBoundKeyOf;


        KeyBindingHelper.registerKeyBinding(ExtraPlayerRenderer.CONFIG_KEY);

        ExtraPlayerRenderer.init();


    }
}
