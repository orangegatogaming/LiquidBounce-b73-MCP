package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "Chams", description = "Allows you to see targets through blocks.", category = ModuleCategory.RENDER)
public class Chams extends Module {
	private final BoolValue targetsValue = new BoolValue("Targets", true);
	private final BoolValue chestsValue = new BoolValue("Chests", true);
	private final BoolValue itemsValue = new BoolValue("Items", true);
}
