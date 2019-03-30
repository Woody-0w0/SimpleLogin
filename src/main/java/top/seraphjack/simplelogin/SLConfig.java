package top.seraphjack.simplelogin;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class SLConfig {
    private static Configuration config;

    SLConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        loadConfig();
    }

    public static Server server = new Server();

    public static Client client = new Client();

    public static class Server {
        public int secs = 60;
    }

    public static class Client {
        public String password = UUID.randomUUID().toString();
    }

    private static void loadConfig() {
        if (config == null) return;
        client.password = config.getString("password","client",UUID.randomUUID().toString(),"Your password");
        server.secs = config.getInt("timeout","server",60,0,Integer.MAX_VALUE,"Login timeout");
    }

    public static class ConfigSyncHandler {
        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.modID.equals(SimpleLogin.MODID)) {
                loadConfig();
            }
        }
    }
}
