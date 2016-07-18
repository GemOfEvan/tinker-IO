package tinker_io.TileEntity.fim;

import net.minecraft.util.math.BlockPos;
/*import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;*/

public interface Adapter
{
	boolean isHeatingItem();
	boolean canFuelTempHeatThisItem(int index);
	boolean isStructureActive();
	boolean isAllItemFinishHeating();
	int getFuelTemp();
	BlockPos getPos();
	
	/*@SideOnly(Side.CLIENT)
	void setFuelTemp(int temp);*/
}