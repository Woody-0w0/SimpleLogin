package top.seraphjack.simplelogin.server;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import top.seraphjack.simplelogin.CommonProxy;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        new CapabilityLoader();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent e) {
        super.serverStarting(e);
        e.registerServerCommand(new SLCommand());
        // Start player login handler
        PlayerLoginHandler.instance();
        MinecraftForge.EVENT_BUS.register(new ServerSideEventHandler());
    }

    @Override
    public void serverStopping(FMLServerStoppingEvent e) {
        super.serverStopping(e);
        PlayerLoginHandler.instance().stop();
    }

    @Override
    public boolean isPhysicalServer() {
        return true;
    }

    @Override
    public boolean isPhysicalClient() {
        return false;
    }
}
