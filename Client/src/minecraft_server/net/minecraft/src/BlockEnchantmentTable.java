package net.minecraft.src;

public class BlockEnchantmentTable extends BlockContainer
{
    protected BlockEnchantmentTable(int par1)
    {
        super(par1, 166, Material.rock);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.field_78031_c);
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int par1, int par2)
    {
        return this.getBlockTextureFromSide(par1);
    }

    /**
     * Returns the block texture based on the side being looked at.  Args: side
     */
    public int getBlockTextureFromSide(int par1)
    {
        return par1 == 0 ? this.blockIndexInTexture + 17 : (par1 == 1 ? this.blockIndexInTexture : this.blockIndexInTexture + 16);
    }

    public TileEntity func_72274_a(World par1World)
    {
        return new TileEntityEnchantmentTable();
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            par5EntityPlayer.displayGUIEnchantment(par2, par3, par4);
            return true;
        }
    }
}
