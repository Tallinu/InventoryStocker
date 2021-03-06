/*******************************************************************************
 * Copyright (c) 2012 Yancarlo Ramsey and CJ Bowman
 * Licensed as open source with restrictions. Please see attached LICENSE.txt.
 ******************************************************************************/

package com.kaijin.AdvInvMan;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StringTranslate;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStocker extends GuiContainer
{
	protected TileEntityStocker tile;
	protected CButton buttonSnap;
	protected CButton buttonMode;
	protected int xLoc;
	protected int yLoc;
	protected int xCenter;

	protected static StringTranslate lang = StringTranslate.getInstance();

	public GuiStocker(InventoryPlayer playerinventory, TileEntityStocker tileentityinventorystocker)
	{
		super(new ContainerStocker(playerinventory, tileentityinventorystocker));
		tile = tileentityinventorystocker;
		xSize = 176;
		ySize = 168;
		buttonSnap = new CButton(0, 0, 0, 50, 13, 201, 1, 201, 16, "", 4210752, 16777120, Info.GUI_PNG);
		buttonMode = new CButton(1, 0, 0, 50, 13, 201, 1, 201, 16, "", 4210752, 16777120, Info.GUI_PNG);
	}

	/**
	 * Called when the GUI is opened and whenever the screen size, scale, layout, or other things change 
	 */
	@Override
	public void initGui()
	{
		super.initGui(); // Don't forget this or MC will crash

		// Upper left corner of GUI panel
		xLoc = (width - xSize) / 2; // Half the difference between screen width and GUI width
		yLoc = (height - ySize) / 2; // Half the difference between screen height and GUI height
		xCenter = width / 2;
		
		buttonSnap.xPosition = xCenter - 24;
		buttonSnap.yPosition = yLoc + 28;
		buttonMode.xPosition = xCenter - 24;
		buttonMode.yPosition = yLoc + 41;
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	protected void drawGuiContainerBackgroundLayer(float par1, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(Info.GUI_PNG);

		this.drawTexturedModalRect(xLoc, yLoc, 0, 0, xSize, ySize);

		Utils.drawCenteredText(fontRenderer, lang.translateKey(tile.getInvName()), xCenter, yLoc + 6, 4210752);
		fontRenderer.drawString(lang.translateKey(Info.KEY_GUI_INPUT), xLoc + 8, yLoc + 17, 4210752);
		Utils.drawRightAlignedText(fontRenderer, lang.translateKey(Info.KEY_GUI_OUTPUT), xLoc + xSize - 8, yLoc + 17, 4210752);

		//Add snapshot text
		if (tile.isSnapshotValid)
		{
			//Utils.drawCenteredGlowingText(fontRenderer, lang.translateKey(Info.KEY_GUI_READY), xCenter, yLoc + 30, 0x0000FF, 0x000040);
			if ((tile.metaInfo & 8) == 8)
			{
				Utils.drawCenteredGlowingText(fontRenderer, lang.translateKey(Info.KEY_GUI_WORKING), xCenter, yLoc + 60, 0x40FF40, 0x082008);
			}
			else
			{
				Utils.drawCenteredGlowingText(fontRenderer, lang.translateKey(Info.KEY_GUI_READY), xCenter, yLoc + 60, 0x40FF40, 0x082008);
			}
		}
		else
		{
			final String line;
			if (tile.hasSnapshot)
			{
				final boolean alternate = (tile.metaInfo & 8) == 8 && (((int)tile.worldObj.getWorldTime()) & 32) == 32;
				line = alternate ? lang.translateKey(Info.KEY_GUI_HALTED) : lang.translateKey(Info.KEY_GUI_INVALID);
			}
			else
			{
				line = lang.translateKey(Info.KEY_GUI_NOSCAN);
			}
			Utils.drawCenteredGlowingText(fontRenderer, line, xCenter, yLoc + 60, 0xFF0000, 0x400000);
		}

		switch (tile.operationMode)
		{
		case NORMAL:
			Utils.drawCenteredGlowingText(fontRenderer, lang.translateKey(Info.KEY_GUI_NORMAL), xCenter, yLoc + 70, 0x40FFFF, 0x082020);
			break;
		case REPLACE:
			Utils.drawCenteredGlowingText(fontRenderer, lang.translateKey(Info.KEY_GUI_REPLACE), xCenter, yLoc + 70, 0x40FFFF, 0x082020);
			break;
		case INSERT:
			Utils.drawCenteredGlowingText(fontRenderer, lang.translateKey(Info.KEY_GUI_INSERT), xCenter, yLoc + 70, 0x40FFFF, 0x082020);
			break;
		case REMOVE:
			Utils.drawCenteredGlowingText(fontRenderer, lang.translateKey(Info.KEY_GUI_REMOVE), xCenter, yLoc + 70, 0x40FFFF, 0x082020);
			break;
		}

		buttonSnap.displayString = lang.translateKey(tile.isSnapshotValid ? Info.KEY_GUI_CLEAR : Info.KEY_GUI_SCAN);
		buttonSnap.drawButton(mc, mouseX, mouseY);
		buttonMode.displayString = lang.translateKey(Info.KEY_GUI_MODE);
		buttonMode.drawButton(mc, mouseX, mouseY);
	}

	//Copied mouseClicked function to get our button to make the "click" noise when clicked
	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		if (par3 == 0)
		{
			if (buttonSnap.enabled && buttonSnap.mousePressed(this.mc, par1, par2))
			{
				mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				this.actionPerformed(buttonSnap);
			}
			else if (buttonMode.enabled && buttonMode.mousePressed(this.mc, par1, par2))
			{
				mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				this.actionPerformed(buttonMode);
			}
		}
		super.mouseClicked(par1, par2, par3);
	}

	/*
	 * This function actually handles what happens when you click on a button, by ID
	 */
	@Override
	public void actionPerformed(GuiButton button)
	{
		tile.sendButtonCommand(button.id);
	}
}
