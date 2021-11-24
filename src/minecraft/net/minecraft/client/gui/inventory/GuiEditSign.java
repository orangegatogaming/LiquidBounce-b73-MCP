package net.minecraft.client.gui.inventory;

import java.awt.*;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import org.lwjgl.input.Keyboard;

public class GuiEditSign extends GuiScreen {
	/**
	 * Reference to the sign object.
	 */
	private TileEntitySign tileSign;

	/**
	 * Counts the number of screen updates.
	 */
	private int updateCounter;

	/**
	 * The index of the line that is being edited.
	 */
	private int editLine;

	/**
	 * "Done" button for the GUI.
	 */
	private GuiButton doneBtn;

	private boolean enabled;

	private GuiButton toggleButton;

	private GuiTextField signCommand1;
	private GuiTextField signCommand2;
	private GuiTextField signCommand3;
	private GuiTextField signCommand4;

	public GuiEditSign(TileEntitySign teSign) {
		this.tileSign = teSign;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		this.buttonList.clear();
		Keyboard.enableRepeatEvents(true);
		this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")));
		this.buttonList.add(toggleButton = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 145, enabled ? "Disable Formatting codes" : "Enable Formatting codes"));
		this.tileSign.setEditable(false);

		this.signCommand1 = new GuiTextField(0, fontRendererObj, this.width / 2 - 100, height - 15, 200, 10);
		this.signCommand2 = new GuiTextField(1, fontRendererObj, this.width / 2 - 100, height - 15 * 2, 200, 10);
		this.signCommand3 = new GuiTextField(2, fontRendererObj, this.width / 2 - 100, height - 15 * 3, 200, 10);
		this.signCommand4 = new GuiTextField(3, fontRendererObj, this.width / 2 - 100, height - 15 * 4, 200, 10);

		this.signCommand1.setText("");
		this.signCommand2.setText("");
		this.signCommand3.setText("");
		this.signCommand4.setText("");
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		NetHandlerPlayClient nethandlerplayclient = this.mc.getNetHandler();

		if (nethandlerplayclient != null) {
			nethandlerplayclient.addToSendQueue(new C12PacketUpdateSign(this.tileSign.getPos(), this.tileSign.signText));
		}

		this.tileSign.setEditable(true);
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		++this.updateCounter;
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		this.signCommand1.mouseClicked(mouseX, mouseY, mouseButton);
		this.signCommand2.mouseClicked(mouseX, mouseY, mouseButton);
		this.signCommand3.mouseClicked(mouseX, mouseY, mouseButton);
		this.signCommand4.mouseClicked(mouseX, mouseY, mouseButton);

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		switch(button.id) {
			case 0:
				if(!signCommand1.getText().isEmpty())
					tileSign.signText[0].setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, signCommand1.getText())));

				if(!signCommand2.getText().isEmpty())
					tileSign.signText[1].setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, signCommand2.getText())));

				if(!signCommand3.getText().isEmpty())
					tileSign.signText[2].setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, signCommand3.getText())));

				if(!signCommand4.getText().isEmpty())
					tileSign.signText[3].setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, signCommand4.getText())));

				tileSign.markDirty();
				this.mc.displayGuiScreen(null);
				break;
			case 1:
				enabled = !enabled;
				toggleButton.displayString = enabled ? "Disable Formatting codes" : "Enable Formatting codes";
				break;
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		this.signCommand1.textboxKeyTyped(typedChar, keyCode);
		this.signCommand2.textboxKeyTyped(typedChar, keyCode);
		this.signCommand3.textboxKeyTyped(typedChar, keyCode);
		this.signCommand4.textboxKeyTyped(typedChar, keyCode);

		if(signCommand1.isFocused() || signCommand2.isFocused() || signCommand3.isFocused() || signCommand4.isFocused())
			return;

		if(keyCode == 200) {
			this.editLine = this.editLine - 1 & 3;
		}

		if(keyCode == 208 || keyCode == 28 || keyCode == 156) {
			this.editLine = this.editLine + 1 & 3;
		}

		String s = this.tileSign.signText[this.editLine].getUnformattedText();
		if(keyCode == 14 && s.length() > 0) {
			s = s.substring(0, s.length() - 1);
		}

		if((ChatAllowedCharacters.isAllowedCharacter(typedChar) || (enabled && typedChar == '§')) && this.fontRendererObj.getStringWidth(s + typedChar) <= 90) {
			s = s + typedChar;
		}

		this.tileSign.signText[this.editLine] = new ChatComponentText(s);
		if(keyCode == 1) {
			this.actionPerformed(this.doneBtn);
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, I18n.format("sign.edit", new Object[0]), this.width / 2, 40, 16777215);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) (this.width / 2), 0.0F, 50.0F);
		float f = 93.75F;
		GlStateManager.scale(-f, -f, -f);
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		Block block = this.tileSign.getBlockType();

		if (block == Blocks.standing_sign) {
			float f1 = (float) (this.tileSign.getBlockMetadata() * 360) / 16.0F;
			GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0625F, 0.0F);
		} else {
			int i = this.tileSign.getBlockMetadata();
			float f2 = 0.0F;

			if (i == 2) {
				f2 = 180.0F;
			}

			if (i == 4) {
				f2 = 90.0F;
			}

			if (i == 5) {
				f2 = -90.0F;
			}

			GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0625F, 0.0F);
		}

		if (this.updateCounter / 6 % 2 == 0) {
			this.tileSign.lineBeingEdited = this.editLine;
		}

		TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
		this.tileSign.lineBeingEdited = -1;
		GlStateManager.popMatrix();
		super.drawScreen(mouseX, mouseY, partialTicks);
		fontRendererObj.drawString("§c§lCommands §7(§f§l1.8§7)", width / 2 - 100, height - 15 * 5, Color.WHITE.getRGB());

		signCommand1.drawTextBox();
		signCommand2.drawTextBox();
		signCommand3.drawTextBox();
		signCommand4.drawTextBox();
	}
}
