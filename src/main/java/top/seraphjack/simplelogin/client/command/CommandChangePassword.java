package top.seraphjack.simplelogin.client.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import top.seraphjack.simplelogin.network.MessageChangePassword;
import top.seraphjack.simplelogin.network.NetworkLoader;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CommandChangePassword extends CommandBase {
    @Override
    public String getCommandName() {
        return "sl_changepassword";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/sl_changepassword <Old Password> <New Password>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 2) {
            MessageChangePassword msg = new MessageChangePassword(args[0], args[1]);
            NetworkLoader.INSTANCE.sendToServer(msg);
        } else {
            throw new CommandException("Invalid arguments.");
        }
    }
}
