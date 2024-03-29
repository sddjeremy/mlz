package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class CommandDefaultGameMode extends CommandGameMode
{
    public String getCommandName()
    {
        return "defaultgamemode";
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.translateString("commands.defaultgamemode.usage", new Object[0]);
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 0)
        {
            EnumGameType var3 = this.getGameModeFromCommand(par1ICommandSender, par2ArrayOfStr[0]);
            this.func_71541_a(var3);
            String var4 = StatCollector.translateToLocal("gameMode." + var3.getName());
            func_71522_a(par1ICommandSender, "commands.defaultgamemode.success", new Object[] {var4});
        }
        else
        {
            throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
        }
    }

    protected void func_71541_a(EnumGameType par1EnumGameType)
    {
        MinecraftServer.getServer().setGameMode(par1EnumGameType);
    }
}
