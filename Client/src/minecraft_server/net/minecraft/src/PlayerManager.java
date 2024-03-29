package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerManager
{
    private final WorldServer field_72701_a;

    /** players in the current instance */
    private final List players = new ArrayList();

    /** the hash of all playerInstances created */
    private final LongHashMap playerInstances = new LongHashMap();

    /** the playerInstances(chunks) that need to be updated */
    private final List playerInstancesToUpdate = new ArrayList();

    /**
     * Number of chunks the server sends to the client. Valid 3<=x<=15. In server.properties.
     */
    private final int playerViewRadius;

    /** x, z direction vectors: east, south, west, north */
    private final int[][] xzDirectionsConst = new int[][] {{1, 0}, {0, 1}, { -1, 0}, {0, -1}};

    public PlayerManager(WorldServer par1WorldServer, int par2)
    {
        if (par2 > 15)
        {
            throw new IllegalArgumentException("Too big view radius!");
        }
        else if (par2 < 3)
        {
            throw new IllegalArgumentException("Too small view radius!");
        }
        else
        {
            this.playerViewRadius = par2;
            this.field_72701_a = par1WorldServer;
        }
    }

    /**
     * Returns the MinecraftServer associated with the PlayerManager.
     */
    public WorldServer getMinecraftServer()
    {
        return this.field_72701_a;
    }

    /**
     * updates all the player instances that need to be updated
     */
    public void updatePlayerInstances()
    {
        Iterator var1 = this.playerInstancesToUpdate.iterator();

        while (var1.hasNext())
        {
            PlayerInstance var2 = (PlayerInstance)var1.next();
            var2.onUpdate();
        }

        this.playerInstancesToUpdate.clear();

        if (this.players.isEmpty())
        {
            WorldProvider var3 = this.field_72701_a.provider;

            if (!var3.canRespawnHere())
            {
                this.field_72701_a.theChunkProviderServer.unloadAllChunks();
            }
        }
    }

    /**
     * passi n the chunk x and y and a flag as to whether or not the instance should be made if it doesnt exist
     */
    private PlayerInstance getPlayerInstance(int par1, int par2, boolean par3)
    {
        long var4 = (long)par1 + 2147483647L | (long)par2 + 2147483647L << 32;
        PlayerInstance var6 = (PlayerInstance)this.playerInstances.getValueByKey(var4);

        if (var6 == null && par3)
        {
            var6 = new PlayerInstance(this, par1, par2);
            this.playerInstances.add(var4, var6);
        }

        return var6;
    }

    public void markBlockNeedsUpdate(int par1, int par2, int par3)
    {
        int var4 = par1 >> 4;
        int var5 = par3 >> 4;
        PlayerInstance var6 = this.getPlayerInstance(var4, var5, false);

        if (var6 != null)
        {
            var6.markBlockNeedsUpdate(par1 & 15, par2, par3 & 15);
        }
    }

    /**
     * Adds an EntityPlayerMP to the PlayerManager.
     */
    public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int var2 = (int)par1EntityPlayerMP.posX >> 4;
        int var3 = (int)par1EntityPlayerMP.posZ >> 4;
        par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
        par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;

        for (int var4 = var2 - this.playerViewRadius; var4 <= var2 + this.playerViewRadius; ++var4)
        {
            for (int var5 = var3 - this.playerViewRadius; var5 <= var3 + this.playerViewRadius; ++var5)
            {
                this.getPlayerInstance(var4, var5, true).addPlayer(par1EntityPlayerMP);
            }
        }

        this.players.add(par1EntityPlayerMP);
        this.func_72691_b(par1EntityPlayerMP);
    }

    public void func_72691_b(EntityPlayerMP par1EntityPlayerMP)
    {
        ArrayList var2 = new ArrayList(par1EntityPlayerMP.loadedChunks);
        int var3 = 0;
        int var4 = this.playerViewRadius;
        int var5 = (int)par1EntityPlayerMP.posX >> 4;
        int var6 = (int)par1EntityPlayerMP.posZ >> 4;
        int var7 = 0;
        int var8 = 0;
        ChunkCoordIntPair var9 = PlayerInstance.func_73253_a(this.getPlayerInstance(var5, var6, true));
        par1EntityPlayerMP.loadedChunks.clear();

        if (var2.contains(var9))
        {
            par1EntityPlayerMP.loadedChunks.add(var9);
        }

        int var10;

        for (var10 = 1; var10 <= var4 * 2; ++var10)
        {
            for (int var11 = 0; var11 < 2; ++var11)
            {
                int[] var12 = this.xzDirectionsConst[var3++ % 4];

                for (int var13 = 0; var13 < var10; ++var13)
                {
                    var7 += var12[0];
                    var8 += var12[1];
                    var9 = PlayerInstance.func_73253_a(this.getPlayerInstance(var5 + var7, var6 + var8, true));

                    if (var2.contains(var9))
                    {
                        par1EntityPlayerMP.loadedChunks.add(var9);
                    }
                }
            }
        }

        var3 %= 4;

        for (var10 = 0; var10 < var4 * 2; ++var10)
        {
            var7 += this.xzDirectionsConst[var3][0];
            var8 += this.xzDirectionsConst[var3][1];
            var9 = PlayerInstance.func_73253_a(this.getPlayerInstance(var5 + var7, var6 + var8, true));

            if (var2.contains(var9))
            {
                par1EntityPlayerMP.loadedChunks.add(var9);
            }
        }
    }

    /**
     * Removes an EntityPlayerMP from the PlayerManager.
     */
    public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int var2 = (int)par1EntityPlayerMP.managedPosX >> 4;
        int var3 = (int)par1EntityPlayerMP.managedPosZ >> 4;

        for (int var4 = var2 - this.playerViewRadius; var4 <= var2 + this.playerViewRadius; ++var4)
        {
            for (int var5 = var3 - this.playerViewRadius; var5 <= var3 + this.playerViewRadius; ++var5)
            {
                PlayerInstance var6 = this.getPlayerInstance(var4, var5, false);

                if (var6 != null)
                {
                    var6.removePlayer(par1EntityPlayerMP);
                }
            }
        }

        this.players.remove(par1EntityPlayerMP);
    }

    private boolean func_72684_a(int par1, int par2, int par3, int par4, int par5)
    {
        int var6 = par1 - par3;
        int var7 = par2 - par4;
        return var6 >= -par5 && var6 <= par5 ? var7 >= -par5 && var7 <= par5 : false;
    }

    /**
     * update chunks around a player being moved by server logic (e.g. cart, boat)
     */
    public void updateMountedMovingPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        int var2 = (int)par1EntityPlayerMP.posX >> 4;
        int var3 = (int)par1EntityPlayerMP.posZ >> 4;
        double var4 = par1EntityPlayerMP.managedPosX - par1EntityPlayerMP.posX;
        double var6 = par1EntityPlayerMP.managedPosZ - par1EntityPlayerMP.posZ;
        double var8 = var4 * var4 + var6 * var6;

        if (var8 >= 64.0D)
        {
            int var10 = (int)par1EntityPlayerMP.managedPosX >> 4;
            int var11 = (int)par1EntityPlayerMP.managedPosZ >> 4;
            int var12 = this.playerViewRadius;
            int var13 = var2 - var10;
            int var14 = var3 - var11;

            if (var13 != 0 || var14 != 0)
            {
                for (int var15 = var2 - var12; var15 <= var2 + var12; ++var15)
                {
                    for (int var16 = var3 - var12; var16 <= var3 + var12; ++var16)
                    {
                        if (!this.func_72684_a(var15, var16, var10, var11, var12))
                        {
                            this.getPlayerInstance(var15, var16, true).addPlayer(par1EntityPlayerMP);
                        }

                        if (!this.func_72684_a(var15 - var13, var16 - var14, var2, var3, var12))
                        {
                            PlayerInstance var17 = this.getPlayerInstance(var15 - var13, var16 - var14, false);

                            if (var17 != null)
                            {
                                var17.removePlayer(par1EntityPlayerMP);
                            }
                        }
                    }
                }

                this.func_72691_b(par1EntityPlayerMP);
                par1EntityPlayerMP.managedPosX = par1EntityPlayerMP.posX;
                par1EntityPlayerMP.managedPosZ = par1EntityPlayerMP.posZ;
            }
        }
    }

    public boolean func_72694_a(EntityPlayerMP par1EntityPlayerMP, int par2, int par3)
    {
        PlayerInstance var4 = this.getPlayerInstance(par2, par3, false);
        return var4 == null ? false : PlayerInstance.func_73258_b(var4).contains(par1EntityPlayerMP) && !par1EntityPlayerMP.loadedChunks.contains(PlayerInstance.func_73253_a(var4));
    }

    public static int func_72686_a(int par0)
    {
        return par0 * 16 - 16;
    }

    static WorldServer func_72692_a(PlayerManager par0PlayerManager)
    {
        return par0PlayerManager.field_72701_a;
    }

    static LongHashMap func_72689_b(PlayerManager par0PlayerManager)
    {
        return par0PlayerManager.playerInstances;
    }

    static List func_72682_c(PlayerManager par0PlayerManager)
    {
        return par0PlayerManager.playerInstancesToUpdate;
    }
}
