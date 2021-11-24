package net.minecraft.client.gui;

import java.io.IOException;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.realms.RealmsBridge;

public class GuiDownloadTerrain extends GuiScreen {
	private NetHandlerPlayClient netHandlerPlayClient;
	private int progress;

	public GuiDownloadTerrain(NetHandlerPlayClient netHandler) {
		this.netHandlerPlayClient = netHandler;
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	public void actionPerformed(GuiButton button){
		if (button.id == 0) {
			boolean flag = this.mc.isIntegratedServerRunning();
			boolean flag1 = this.mc.isConnectedToRealms();
			button.enabled = false;
			this.mc.theWorld.sendQuittingDisconnectingPacket();
			this.mc.loadWorld(null);

			if (flag) {
				this.mc.displayGuiScreen(new GuiMainMenu());
			} else if (flag1) {
				RealmsBridge realmsbridge = new RealmsBridge();
				realmsbridge.switchToRealms(new GuiMainMenu());
			} else {
				this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
			}
		}
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		++this.progress;

		if (this.progress % 20 == 0) {
			this.netHandlerPlayClient.addToSendQueue(new C00PacketKeepAlive());
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground(0);
		this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingTerrain", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}
}
