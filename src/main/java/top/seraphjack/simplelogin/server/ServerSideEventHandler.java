package top.seraphjack.simplelogin.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.network.MessageRequestLogin;
import top.seraphjack.simplelogin.network.NetworkLoader;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.capability.CapabilityPassword;
import top.seraphjack.simplelogin.server.capability.IPassword;

public class ServerSideEventHandler {

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerLoginHandler.instance().addPlayerToLoginList((EntityPlayerMP) event.player);
        NetworkLoader.INSTANCE.sendTo(new MessageRequestLogin(), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onAttachCapabilitiesEntity(AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(SimpleLogin.MODID, "sl_password"),
                    new CapabilityPassword.PlayerProvider());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        Capability<IPassword> capability = CapabilityLoader.CAPABILITY_PASSWORD;
        Capability.IStorage<IPassword> storage = capability.getStorage();

        if (event.original.hasCapability(capability, null) && event.entityPlayer.hasCapability(capability, null)) {
            NBTBase nbt = storage.writeNBT(capability, event.original.getCapability(capability, null), null);
            storage.readNBT(capability, event.original.getCapability(capability, null), null, nbt);
        }
    }

    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        if (PlayerLoginHandler.instance().isPlayerInLoginList(event.sender.getName())) {
            event.setCanceled(true);
            try {
                CommandBase.getCommandSenderAsPlayer(event.sender).addChatMessage(new ChatComponentText("You should login first."));
            } catch (PlayerNotFoundException ignore) {
            }
            SimpleLogin.logger.warn("Player " + event.sender.getName() + " tried to use command before login.");
        }
    }
}
