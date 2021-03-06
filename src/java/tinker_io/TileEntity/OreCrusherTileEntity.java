package tinker_io.TileEntity;


import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import tinker_io.handler.OreCrusherBanList;
import tinker_io.registry.RegisterUtil;

public class OreCrusherTileEntity extends TileEntityContainerAdapter implements ITickable, IEnergyReceiver  {
	public OreCrusherTileEntity() {
		super(null, 6);
	}

	final int[] slotsSpeedUPG = new int[] { 0 };
	final int[] slotsOre = new int[] { 1 };
	final int[] slotsProduct = new int[] { 2 };
	final int[] slotsFortuneUPG1 = new int[] { 3 };
	final int[] slotsFortuneUPG2 = new int[] { 4 };
	final int[] slotsFortuneUPG3 = new int[] { 5 };
	
	protected EnergyStorage storage = new EnergyStorage(100000, 2000, 0);
	
	private int speed = 300;
	
	private int crushTime = 0;
	
	//private ItemStack[] getSlots() = this.getSlots();
	
	private int doubleProductRate;
	
	private String nameOreCrusher;
	
	public void nameOreCrusher(String string){
		this.nameOreCrusher = string;
	}

	@Override
	public String getName() {
		return this.hasCustomName() ? this.nameOreCrusher : I18n.format("tile.Ore_Crusher.name");
	}

	@Override
	public boolean hasCustomName() {
		return this.nameOreCrusher != null && this.nameOreCrusher.length() > 0;
	}

	//Slot
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] slot = new int[]{this.slotsProduct[0], this.slotsOre[0]};
		return slot;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		if(index != slotsOre[0]){
			return false;
		}
		OreCrusherBanList banList = OreCrusherBanList.banedOreDicList();
		return this.isOreInOreDic(itemStackIn) && banList.canItemCrush(itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if(index == slotsProduct[0]){
			return true;
		}
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack itemstack) {
		OreCrusherBanList banList = OreCrusherBanList.banedOreDicList();
		if(this.isOreInOreDic(itemstack) && banList.canItemCrush(itemstack)){
			return true;
		}
		return false;
	}
	
	//Gui
	@SideOnly(Side.CLIENT)
	public int getCrushProgressScaled(int par1) {
		return this.crushTime * par1 / speed;
	}
	
	@SideOnly(Side.CLIENT)
	public int getEnergyBar(int par1) {
		return this.storage.getEnergyStored() * par1 / this.storage.getMaxEnergyStored();
	}
	
	public void update() {
		if (world.isRemote) return;
		crush();
		this.notifyBlockUpdate();
	}
	public void updateEntity() {
		//crush();
	}
	
	/**
	 * Return true if the item stack is not null and is an ore registered in Ore Dictionary. 
	 * 
	 * @param itemStack
	 * @return
	 */
	public boolean isOreInOreDic(ItemStack itemStack){
		if(itemStack != null && !itemStack.isEmpty()){
			if(OreDictionary.getOreIDs(itemStack).length > 0){
				int oreID = OreDictionary.getOreIDs(itemStack)[0];
				String oreName = OreDictionary.getOreName(oreID);
				String title = oreName.substring(0, 3);
				if(title.equals("ore")){
					return true;
				}else{
					
					return false;
				}
			}else{
				
				return false;
			}
		}else{
			return false;
		}
	}
	
	public String getOreDicName(ItemStack itemStack){
		String oreDicName = null;
		if(itemStack != null && !itemStack.isEmpty() && OreDictionary.getOreIDs(itemStack).length > 0){
			int oreID = OreDictionary.getOreIDs(itemStack)[0];
			oreDicName = OreDictionary.getOreName(oreID);
		}
		return oreDicName;
	}
	
	private boolean isOreDicNBTTagEqual(ItemStack itemStack1, ItemStack itemStack2){
		if(itemStack1 != null && !itemStack1.isEmpty() && itemStack2 != null && !itemStack2.isEmpty() && itemStack1.getTagCompound() != null && itemStack2.getTagCompound() != null){
			NBTTagCompound nbt1 = itemStack1.getTagCompound();
			NBTTagCompound nbt2 = itemStack2.getTagCompound();
			if(nbt1.getString("oreDic").equals(nbt2.getString("oreDic"))){
				return true;
			}
		}
		return false;
	}
	
	private ItemStack getProduct(){
		int productAmount = 2;
		
		if(canGetMoreProduct()){
			productAmount = 3;
		}
		ItemStack product = new ItemStack(RegisterUtil.CrushedOre, productAmount);
		product.setTagCompound(new NBTTagCompound());
		NBTTagCompound nbt = product.getTagCompound();
		if(isOreInOreDic(getSlots()[1])){
			nbt.setString("oreDic", getOreDicName(getSlots()[1]));
		}
		return product;
	}
	
	private boolean canGetMoreProduct(){
		int bookAmount = 0;
		
		ItemStack slotForutne1 = this.getSlots()[3];
		ItemStack slotForutne2 = this.getSlots()[4];
		ItemStack slotForutne3 = this.getSlots()[5];
		
		if(isFortuenEnchantedBook(slotForutne1)){
			bookAmount = bookAmount+2;
		}
		
		if(isFortuenEnchantedBook(slotForutne2)){
			bookAmount = bookAmount+2;
		}
		
		if(slotForutne3 != null && slotForutne3.isItemEqual(new ItemStack(RegisterUtil.Upgrade, 1, 6))){
			bookAmount = bookAmount + 3;
		}
		
		doubleProductRate = 30 + bookAmount * 10;
		
		int rate = (int)(Math.random()*99);
		
		if(rate <= doubleProductRate){
			return true;
		}
		
		return false;
	}
	
	public boolean isFortuenEnchantedBook(ItemStack itemstack){
		if(itemstack != null){
			if(getEnchantID(itemstack) == 35){
				return true;
			}
		}
		return false;
	}
	
	private int getEnchantID(ItemStack itemstack){
		if(itemstack != null && itemstack.getItem() instanceof ItemEnchantedBook){
			ItemEnchantedBook book = (ItemEnchantedBook) itemstack.getItem();
			NBTTagCompound tag = (NBTTagCompound) book.getEnchantments(itemstack).get(0);
			if(tag != null){
				return tag.getInteger("id");
			}
		}
		return 0;
	}
	
	private boolean canCrush(){
		ItemStack product = this.getProduct();
		OreCrusherBanList banList = OreCrusherBanList.banedOreDicList();
		
		if(getSlots()[1] != null && getSlots()[1].isEmpty()){
			return false;
		}
		
		if(!isOreInOreDic(getSlots()[1]) || !banList.canItemCrush(getSlots()[1])){
			return false;
		}
		
		if(getSlots()[2] != null && getSlots()[2].isEmpty() && this.storage.getEnergyStored() > speed){
			return true;
		}else if(getSlots()[2] != null && getSlots()[2].isItemEqual(product) && isOreDicNBTTagEqual(getSlots()[2], product) && this.storage.getEnergyStored() > speed){
			if(this.getSlots()[2].getCount() <= product.getMaxStackSize() - 3){
				return true;
			}
		}
		return false;
	}
	
	public int getRate(){
		return doubleProductRate;
	}
	
	public void crush(){
		ItemStack Product = this.getProduct();
		
		if(canCrush()){
			if(crushTime >= speed){
				crushTime = 0;
				if (this.getSlots()[2] != null && this.getSlots()[2].isEmpty()) {
					this.getSlots()[2] = Product.copy();
				} else if (this.getSlots()[2].getItem() == Product.getItem()) {
					//this.getSlots()[2].stackSize += Product.stackSize;
					this.getSlots()[2].grow(Product.getCount());
				}
				if(getSlots()[1].getCount() == 1){
					getSlots()[1] = ItemStack.EMPTY;
				}else{
					//--getSlots()[1].stackSize;
					getSlots()[1].grow(-1);
				}
			}else{
				crushTime++;
				speedUPG();
				this.storage.setEnergyStored(this.storage.getEnergyStored() - 45);
			}
		}else{
			crushTime = 0;
		}
	}
	
	private void speedUPG(){
		ItemStack stackSpeedUPG = new ItemStack(RegisterUtil.SpeedUPG);
		
		if(this.getSlots()[0] != null && this.getSlots()[0].isEmpty()){

		}else{
			if(this.getSlots()[0].isItemEqual(stackSpeedUPG)){
				crushTime = crushTime+(this.getSlots()[0].getCount()/3/2);
			}
		}
	}
	
	public boolean isActive(){
		return this.canCrush();
	}
	
	//NBT
	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		this.crushTime = tagCompound.getShort("CrushTime");
		this.doubleProductRate = tagCompound.getInteger("Rate");
		
		/*NBTTagList tagList = tagCompound.getTagList("Items", 10);
		this.getSlots() = new ItemStack[this.getSizeInventory()];
		
		for (int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound tabCompound1 = tagList.getCompoundTagAt(i);
			byte byte0 = tabCompound1.getByte("Slot");
			
			if (byte0 >= 0 && byte0 < this.getSlots().length) {
				this.getSlots()[byte0] = ItemStack.loadItemStackFromNBT(tabCompound1);
			}
		}*/

		if (tagCompound.hasKey("CustomName", 8)) {
			this.nameOreCrusher = tagCompound.getString("CustomName");
		}
		storage.readFromNBT(tagCompound);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setShort("CrushTime", (short) this.crushTime);
		tagCompound.setInteger("Rate", this.doubleProductRate);
		
		/*NBTTagList tagList = new NBTTagList();

		for (int i = 0; i < this.getSlots().length; ++i) {
			if (this.getSlots()[i] != null) {
				NBTTagCompound tagCompound1 = new NBTTagCompound();
				tagCompound1.setByte("Slot", (byte) i);
				this.getSlots()[i].writeToNBT(tagCompound1);
				tagList.appendTag(tagCompound1);
			}
		}

		tagCompound.setTag("Items", tagList);
		if (this.hasCustomName()) {
			tagCompound.setString("CustomName", this.nameOreCrusher);
		}*/
		
		storage.writeToNBT(tagCompound);
		
		return tagCompound;
	}
	
	//Packet
	 @Override
	 public SPacketUpdateTileEntity getUpdatePacket() {	 
	     NBTTagCompound tag = new NBTTagCompound();
	     this.writeToNBT(tag);
	     return new SPacketUpdateTileEntity(pos, 1, tag);
	 }
	 
	 @Override
	 public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
	     readFromNBT(packet.getNbtCompound());
	 }

	//RF Energy
	@Override
	public int getEnergyStored(EnumFacing from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return this.storage.receiveEnergy(Math.min(storage.getMaxReceive(), maxReceive), simulate);
	}
	
	public EnergyStorage getStorage(){
		return this.storage;
	}
	
	private void notifyBlockUpdate(){
		if(world!=null && pos != null){
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 3);
		}
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
		
}
