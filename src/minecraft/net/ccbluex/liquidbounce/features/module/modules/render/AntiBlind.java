package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "AntiBlind", description = "Cancels blindness effects.", category = ModuleCategory.RENDER)
public class AntiBlind extends Module {
	public BoolValue confusionEffect = new BoolValue("Confusion", true);
	public BoolValue pumpkinEffect = new BoolValue("Pumpkin", true);
	public BoolValue fireEffect = new BoolValue("Fire", false);
}
