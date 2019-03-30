package top.seraphjack.simplelogin;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import top.seraphjack.simplelogin.network.NetworkLoader;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class CommonProxy {
    @OverridingMethodsMustInvokeSuper
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new SLConfig.ConfigSyncHandler());
        new SLConfig(event);
    }

    @OverridingMethodsMustInvokeSuper
    public void init() {
        new NetworkLoader();
    }

    @OverridingMethodsMustInvokeSuper
    public void serverStarting(FMLServerStartingEvent e) {

    }

    @OverridingMethodsMustInvokeSuper
    public void serverStopping(FMLServerStoppingEvent e) {

    }

    public abstract boolean isPhysicalServer();

    public abstract boolean isPhysicalClient();
}
