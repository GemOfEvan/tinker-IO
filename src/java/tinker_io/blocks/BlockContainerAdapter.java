package tinker_io.blocks;

import tinker_io.TileEntity.TileEntityContainerAdapter;
import tinker_io.main.Main;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockContainerAdapter extends BlockContainer{
	
	public final static int renderTypeForStandardBlockModels = 3;
	
	protected BlockContainerAdapter() {
		super(Material.rock);
		setHarvestLevel("pickaxe", 1);
		setHardness(3);
		setCreativeTab(Main.TinkerIOTabs);
	}
	
	 @Override public abstract boolean onBlockActivated(
			 World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			 EnumFacing side, float hitX, float hitY, float hitZ);
	
	  @Override
	  public boolean hasTileEntity(IBlockState state) {
	    return true;
	  }
	  
	/**
     * The type of render function called.
     * 3 for standard block models,
     * 2 for TESR's,
     * 1 for liquids,
     * -1 is no render.
     * 
     * We need number 3.
     */
	@Override
    public int getRenderType()
    {
        return renderTypeForStandardBlockModels;
    }
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
	    TileEntity tile = worldIn.getTileEntity(pos);

	    if(tile instanceof IInventory) {
	      InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tile);
	      worldIn.updateComparatorOutputLevel(pos, this);
	    }
	    super.breakBlock(worldIn, pos, state);
	  }

}
