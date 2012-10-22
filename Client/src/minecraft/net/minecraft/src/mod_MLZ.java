package net.minecraft.src;

public class mod_MLZ extends BaseMod
{
	public static ItemMCH mch = (ItemMCH)(new ItemMCH(128).setItemName("mch"));
	
	public void load()
	{
		this.addRecipes();
		ModLoader.addName(mch, "Mind-Control Helmet");
		mch.iconIndex = ModLoader.addOverride("/gui/items.png", "/mchelmet.png");
	}
	
	public void addRecipes()
	{
		ModLoader.addRecipe(new ItemStack(mch, 1), new Object[] { "R", "H", 'R', Block.torchRedstoneActive, 'H', Item.helmetSteel});
	}
	
	public String getVersion()
	{
		return "1.0";
	}
}
