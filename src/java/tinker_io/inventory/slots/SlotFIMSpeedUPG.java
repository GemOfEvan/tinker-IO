package tinker_io.inventory.slots;

import tinker_io.registry.ItemRegistry;
import tinker_io.registry.RegisterUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFIMSpeedUPG extends Slot{

	public SlotFIMSpeedUPG(
			IInventory inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(
			ItemStack stack)
	{
		return stack.isItemEqual(new ItemStack(RegisterUtil.SpeedUPG));
	}
}
