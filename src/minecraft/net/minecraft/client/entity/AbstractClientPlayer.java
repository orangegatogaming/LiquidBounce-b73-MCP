package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;

import java.io.File;
import java.util.Objects;

import net.ccbluex.liquidbounce.cape.CapeAPI;
import net.ccbluex.liquidbounce.cape.CapeInfo;
import net.ccbluex.liquidbounce.features.module.modules.misc.NameProtect;
import net.ccbluex.liquidbounce.features.module.modules.render.NoFOV;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public abstract class AbstractClientPlayer extends EntityPlayer {
	private NetworkPlayerInfo playerInfo;

	public AbstractClientPlayer(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
	}

	/**
	 * Returns true if the player is in spectator mode.
	 */
	public boolean isSpectator() {
		NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
		return networkplayerinfo != null && networkplayerinfo.getGameType() == WorldSettings.GameType.SPECTATOR;
	}

	/**
	 * Checks if this instance of AbstractClientPlayer has any associated player data.
	 */
	public boolean hasPlayerInfo() {
		return this.getPlayerInfo() != null;
	}

	protected NetworkPlayerInfo getPlayerInfo() {
		if (this.playerInfo == null) {
			this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
		}

		return this.playerInfo;
	}

	/**
	 * Returns true if the player has an associated skin.
	 */
	public boolean hasSkin() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
	}

	/**
	 * Returns true if the player instance has an associated skin.
	 */
	public ResourceLocation getLocationSkin() {
		final NameProtect nameProtect = NameProtect.getInstance();

		if(nameProtect.getState() && nameProtect.skinProtectValue.get()) {
			if (nameProtect.allPlayersValue.get() && !Objects.equals(getGameProfile().getName(), Minecraft.getMinecraft().thePlayer.getGameProfile().getName()))
				return DefaultPlayerSkin.getDefaultSkin(getUniqueID());
		}

		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
	}

	private CapeInfo capeInfo;

	public ResourceLocation getLocationCape() {
		if(CapeAPI.INSTANCE.hasCapeService()){
			if (capeInfo == null)
				capeInfo = CapeAPI.INSTANCE.loadCape(getUniqueID());

			if(capeInfo != null && capeInfo.isCapeAvailable())
				return capeInfo.getResourceLocation();
		}

		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
	}

	public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		ITextureObject itextureobject = texturemanager.getTexture(resourceLocationIn);

		if (itextureobject == null) {
			itextureobject = new ThreadDownloadImageData((File) null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[]{StringUtils.stripControlCodes(username)}), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), new ImageBufferDownload());
			texturemanager.loadTexture(resourceLocationIn, itextureobject);
		}

		return (ThreadDownloadImageData) itextureobject;
	}

	/**
	 * Returns true if the username has an associated skin.
	 */
	public static ResourceLocation getLocationSkin(String username) {
		return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
	}

	public String getSkinType() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
	}

	public float getFovModifier() {
		final NoFOV fovModule = NoFOV.Companion.getInstance();

		if(fovModule.getState()) {
			float newFOV = fovModule.getFovValue().get();

			if(!this.isUsingItem()) {
				return newFOV;
			}

			if(this.getItemInUse().getItem() != Items.bow) {
				return newFOV;
			}

			int i = this.getItemInUseDuration();
			float f1 = (float) i / 20.0f;
			f1 = f1 > 1.0f ? 1.0f : f1 * f1;
			newFOV *= 1.0f - f1 * 0.15f;
			return newFOV;
		}

		float f = 1.0F;

		if (this.capabilities.isFlying) {
			f *= 1.1F;
		}

		IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		f = (float) ((double) f * ((iattributeinstance.getAttributeValue() / (double) this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

		if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
			int i = this.getItemInUseDuration();
			float f1 = (float) i / 20.0F;

			if (f1 > 1.0F) {
				f1 = 1.0F;
			} else {
				f1 = f1 * f1;
			}

			f *= 1.0F - f1 * 0.15F;
		}

		return f;
	}
}
