package top.seraphjack.simplelogin.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SLCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "simplelogin";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/simplelogin reset <PlayerName>\n" +
                "/simplelogin list";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw (new WrongUsageException("/simplelogin reset <PlayerName>"));
        } else {
            switch (args[0]) {
                case "reset": {
                    PlayerLoginHandler.instance().resetPassword(args[1]);
                    sender.addChatMessage(new ChatComponentText("Player " + args[1] + " has been added to resetPassword list."));
                    break;
                }
                case "list": {
                    sender.addChatMessage(new ChatComponentText(PlayerLoginHandler.instance().getResetPasswordUsers()));
                    break;
                }
                default: {
                    throw (new WrongUsageException("/simplelogin reset <PlayerName>"));
                }
            }
        }
    }
}
