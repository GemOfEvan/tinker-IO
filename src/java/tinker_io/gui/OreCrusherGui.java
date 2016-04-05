package tinker_io.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import tinker_io.TileEntity.OreCrusherTileEntity;
import tinker_io.inventory.ContainerOreCrusher;
import tinker_io.main.Main;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class OreCrusherGui extends GuiContainer{
	private static final ResourceLocation OCGuiTextures = new ResourceLocation(Main.MODID, "textures/gui/OreCrusher.png");
	public World world;
	private OreCrusherTileEntity tileOC;

	
	public OreCrusherGui(InventoryPlayer invPlayer, OreCrusherTileEntity tile) {
		super(new ContainerOreCrusher(invPlayer, tile));
		this.tileOC = tile;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int cornerX = (width - xSize) / 2;
	    int cornerY = (height - ySize) / 2;
		
		String string = this.tileOC.hasCustomName() ? this.tileOC.getName() : I18n.format(this.tileOC.getName(), new Object[0]);
		
		this.fontRendererObj.drawString(string, (this.xSize - this.fontRendererObj.getStringWidth(string))/2, 6, 4210752);
		
		//ToolTip
		List<String> text = Lists.newArrayList();
    	text.add(EnumChatFormatting.WHITE.toString() + StatCollector.translateToLocal("tio.toolTips.oreCrusher.energy"));
    	text.add(EnumChatFormatting.WHITE.toString() + tileOC.getEnergyStored(null) + " / " + tileOC.getMaxEnergyStored(null)+ " " + StatCollector.translateToLocal("tio.toolTips.oreCrusher.rf"));
    	
    	if(mouseX >= cornerX + 11 && mouseX <= cornerX + 21 && mouseY <= cornerY + 67 && mouseY >= cornerY + 67 - 54){
    		this.drawHoveringText(text, mouseX-cornerX, mouseY-cornerY);
        }
		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		int cornerX = (width - xSize) / 2;
	    int cornerY = (height - ySize) / 2;
		
		 GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        this.mc.getTextureManager().bindTexture(OCGuiTextures);
	        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);
	        
	        //Process Bar
	        int i1;
	        i1 = this.tileOC.getCrushProgressScaled(24);
	        this.drawTexturedModalRect(cornerX + 81, cornerY + 35, 176, 14, i1 + 1, 16);
	        
	        //Energy Bar
	        int energyBar = tileOC.getEnergyBar(54);
	        this.drawTexturedModalRect(cornerX + 11, cornerY + 13 + 54 - energyBar, 178, 34, 10, energyBar);
	}

}
