package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class PlayerInstance
{
    /** the list of all players in this instance (chunk) */
    private final List players;

    /** the chunk the player currently resides in */
    private final ChunkCoordIntPair currentChunk;

    /** array of blocks to update this tick */
    private short[] blocksToUpdate;

    /** the number of blocks that need to be updated next tick */
    private int numBlocksToUpdate;
    private int field_73260_f;

    final PlayerManager playerManager;

    public PlayerInstance(PlayerManager par1PlayerManager, int par2, int par3)
    {
        this.playerManager = par1PlayerManager;
        this.players = new ArrayList();
        this.blocksToUpdate = new short[64];
        this.numBlocksToUpdate = 0;
        this.currentChunk = new ChunkCoordIntPair(par2, par3);
        par1PlayerManager.getMinecraftServer().theChunkProviderServer.loadChunk(par2, par3);
    }

    /**
     * adds this player to the playerInstance
     */
    public void addPlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.players.contains(par1EntityPlayerMP))
        {
            throw new IllegalStateException("Failed to add player. " + par1EntityPlayerMP + " already is in chunk " + this.currentChunk.chunkXPos + ", " + this.currentChunk.chunkZPos);
        }
        else
        {
            this.players.add(par1EntityPlayerMP);
            par1EntityPlayerMP.loadedChunks.add(this.currentChunk);
        }
    }

    /**
     * remove player from this instance
     */
    public void removePlayer(EntityPlayerMP par1EntityPlayerMP)
    {
        if (this.players.contains(par1EntityPlayerMP))
        {
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new Packet51MapChunk(PlayerManager.func_72692_a(this.playerManager).getChunkFromChunkCoords(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos), true, 0));
            this.players.remove(par1EntityPlayerMP);
            par1EntityPlayerMP.loadedChunks.remove(this.currentChunk);

            if (this.players.isEmpty())
            {
                long var2 = (long)this.currentChunk.chunkXPos + 2147483647L | (long)this.currentChunk.chunkZPos + 2147483647L << 32;
                PlayerManager.func_72689_b(this.playerManager).remove(var2);

                if (this.numBlocksToUpdate > 0)
                {
                    PlayerManager.func_72682_c(this.playerManager).remove(this);
                }

                this.playerManager.getMinecraftServer().theChunkProviderServer.dropChunk(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos);
            }
        }
    }

    /**
     * mark the block as changed so that it will update clients who need to know about it
     */
    public void markBlockNeedsUpdate(int par1, int par2, int par3)
    {
        if (this.numBlocksToUpdate == 0)
        {
            PlayerManager.func_72682_c(this.playerManager).add(this);
        }

        this.field_73260_f |= 1 << (par2 >> 4);

        if (this.numBlocksToUpdate < 64)
        {
            short var4 = (short)(par1 << 12 | par3 << 8 | par2);

            for (int var5 = 0; var5 < this.numBlocksToUpdate; ++var5)
            {
                if (this.blocksToUpdate[var5] == var4)
                {
                    return;
                }
            }

            this.blocksToUpdate[this.numBlocksToUpdate++] = var4;
        }
    }

    /**
     * sends the packet to all players in the current instance
     */
    public void sendPacketToPlayersInInstance(Packet par1Packet)
    {
        Iterator var2 = this.players.iterator();

        while (var2.hasNext())
        {
            EntityPlayerMP var3 = (EntityPlayerMP)var2.next();

            if (!var3.loadedChunks.contains(this.currentChunk))
            {
                var3.playerNetServerHandler.sendPacket(par1Packet);
            }
        }
    }

    public void onUpdate()
    {
        if (this.numBlocksToUpdate != 0)
        {
            int var1;
            int var2;
            int var3;

            if (this.numBlocksToUpdate == 1)
            {
                var1 = this.currentChunk.chunkXPos * 16 + (this.blocksToUpdate[0] >> 12 & 15);
                var2 = this.blocksToUpdate[0] & 255;
                var3 = this.currentChunk.chunkZPos * 16 + (this.blocksToUpdate[0] >> 8 & 15);
                this.sendPacketToPlayersInInstance(new Packet53BlockChange(var1, var2, var3, PlayerManager.func_72692_a(this.playerManager)));

                if (PlayerManager.func_72692_a(this.playerManager).blockHasTileEntity(var1, var2, var3))
                {
                    this.updateTileEntity(PlayerManager.func_72692_a(this.playerManager).getBlockTileEntity(var1, var2, var3));
                }
            }
            else
            {
                int var4;

                if (this.numBlocksToUpdate == 64)
                {
                    var1 = this.currentChunk.chunkXPos * 16;
                    var2 = this.currentChunk.chunkZPos * 16;
                    this.sendPacketToPlayersInInstance(new Packet51MapChunk(PlayerManager.func_72692_a(this.playerManager).getChunkFromChunkCoords(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos), false, this.field_73260_f));

                    for (var3 = 0; var3 < 16; ++var3)
                    {
                        if ((this.field_73260_f & 1 << var3) != 0)
                        {
                            var4 = var3 << 4;
                            List var5 = PlayerManager.func_72692_a(this.playerManager).getTileEntityList(var1, var4, var2, var1 + 16, var4 + 16, var2 + 16);
                            Iterator var6 = var5.iterator();

                            while (var6.hasNext())
                            {
                                TileEntity var7 = (TileEntity)var6.next();
                                this.updateTileEntity(var7);
                            }
                        }
                    }
                }
                else
                {
                    this.sendPacketToPlayersInInstance(new Packet52MultiBlockChange(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos, this.blocksToUpdate, this.numBlocksToUpdate, PlayerManager.func_72692_a(this.playerManager)));

                    for (var1 = 0; var1 < this.numBlocksToUpdate; ++var1)
                    {
                        var2 = this.currentChunk.chunkXPos * 16 + (this.blocksToUpdate[var1] >> 12 & 15);
                        var3 = this.blocksToUpdate[var1] & 255;
                        var4 = this.currentChunk.chunkZPos * 16 + (this.blocksToUpdate[var1] >> 8 & 15);

                        if (PlayerManager.func_72692_a(this.playerManager).blockHasTileEntity(var2, var3, var4))
                        {
                            this.updateTileEntity(PlayerManager.func_72692_a(this.playerManager).getBlockTileEntity(var2, var3, var4));
                        }
                    }
                }
            }

            this.numBlocksToUpdate = 0;
            this.field_73260_f = 0;
        }
    }

    /**
     * sends players update packet about the given entity
     */
    private void updateTileEntity(TileEntity par1TileEntity)
    {
        if (par1TileEntity != null)
        {
            Packet var2 = par1TileEntity.getDescriptionPacket();

            if (var2 != null)
            {
                this.sendPacketToPlayersInInstance(var2);
            }
        }
    }

    static ChunkCoordIntPair func_73253_a(PlayerInstance par0PlayerInstance)
    {
        return par0PlayerInstance.currentChunk;
    }

    static List func_73258_b(PlayerInstance par0PlayerInstance)
    {
        return par0PlayerInstance.players;
    }
}
