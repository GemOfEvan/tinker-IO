package tinker_io.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tinker_io.TileEntity.SOTileEntity;

public class VoidLiquidPacket implements IMessage{
	
	//private int amount;
	
	private NBTTagCompound data;	
	
	public VoidLiquidPacket(){}
	
	public VoidLiquidPacket(int[] coord){
		data = new NBTTagCompound();
		data.setIntArray("Coord", coord);
	}
	
	
	public void fromBytes(ByteBuf buf) {
		// TODO
		//amount = buf.readInt();
		data = ByteBufUtils.readTag(buf);

	}

	public void toBytes(ByteBuf buf) {
		// TODO
		//buf.writeInt(amount);
		 ByteBufUtils.writeTag(buf, data);
	}
	public static class Handler extends AbstractServerMessageHandler<VoidLiquidPacket> {
		boolean DEBUG = false;
		
		@Override
		public IMessage handleServerMessage(EntityPlayer player, VoidLiquidPacket message, MessageContext ctx) {
			/*MovingObjectPosition mop = Minecraft.getMinecraft().renderViewEntity.rayTrace(200, 1.0F);
			if(mop != null){
				int blockHitSide = mop.sideHit;
				int x = mop.blockX;
				int y = mop.blockY;
				int z = mop.blockZ;
				
				if(player.worldObj.isRemote == false){
					SOTileEntity tileSO = (SOTileEntity) player.worldObj.getTileEntity(x, y, z);
					if(tileSO != null){
						tileSO.voidLiquid();
						if(DEBUG==true) System.out.println("[Tinker I/O] [DEBUG] voided!");
					}else{
						System.out.println("[Tinker I/O] [Error] Failed to get the TileEntity of the block !");
					}
				}
			}else{
				System.out.println("[Tinker I/O] [Error] Failed to get the coord of the block !");
			}*/
			
			int x = message.data.getIntArray("Coord")[0];
			int y = message.data.getIntArray("Coord")[1];
			int z = message.data.getIntArray("Coord")[2];
			
			if(player.world.isRemote == false){
				SOTileEntity tileSO = (SOTileEntity) player.world.getTileEntity(new BlockPos(x, y, z));
				if(tileSO != null){
					tileSO.voidLiquid();
					if(DEBUG==true) System.out.println("[Tinker I/O] [DEBUG] voided!");
				}else{
					System.out.println("[Tinker I/O] [Error] Failed to get the TileEntity of the block !");
				}
			}
			
			
			
			 return null;
		}
		
	}
}

