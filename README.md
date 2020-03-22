# Extra Player Renderer

Render a player entity on the bottom left corner of he screen.

The Chat HUD will be moved upward, so that it won't be blocked.

This mod is fully developed, so it's released under license CC0-1.0, free free to modify.

To avoid conflict with [tweakeroo](https://github.com/maruohon/tweakeroo), you can open the jar of tweakeroo, find `mixins.tweakeroo.json`, and delete the following line: `"MixinChatHUD",`. Then put the modified file back to jar. 

The trade-back of this modification is you can no longer use `tweakChatTimestamp` and `tweakChatBackgroundColor` functions.

This mod is compatible with Gamepiaynmo's [CustomPlayerModel](https://github.com/Gamepiaynmo/CustomModel) mod, but the compatibility is not ensured if you use models smaller or larger than a normal player model.