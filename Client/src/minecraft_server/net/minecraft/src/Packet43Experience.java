package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet43Experience extends Packet
{
    /** The current experience bar points. */
    public float experience;

    /** The total experience points. */
    public int experienceTotal;

    /** The experience level. */
    public int experienceLevel;

    public Packet43Experience() {}

    public Packet43Experience(float par1, int par2, int par3)
    {
        this.experience = par1;
        this.experienceTotal = par2;
        this.experienceLevel = par3;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.experience = par1DataInputStream.readFloat();
        this.experienceLevel = par1DataInputStream.readShort();
        this.experienceTotal = par1DataInputStream.readShort();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeFloat(this.experience);
        par1DataOutputStream.writeShort(this.experienceLevel);
        par1DataOutputStream.writeShort(this.experienceTotal);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleExperience(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 4;
    }

    public boolean func_73278_e()
    {
        return true;
    }

    public boolean func_73268_a(Packet par1Packet)
    {
        return true;
    }

    public boolean func_73277_a_()
    {
        return true;
    }
}
