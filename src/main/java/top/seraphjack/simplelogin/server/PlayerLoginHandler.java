package top.seraphjack.simplelogin.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.seraphjack.simplelogin.SLConfig;
import top.seraphjack.simplelogin.SimpleLogin;
import top.seraphjack.simplelogin.server.capability.CapabilityLoader;
import top.seraphjack.simplelogin.server.capability.IPassword;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@SideOnly(Side.SERVER)
public class PlayerLoginHandler {
    private static Thread PLAYER_HANDLER_THREAD;
    private static PlayerLoginHandler INSTANCE;

    private boolean alive;
    private ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Login> loginList = new ConcurrentLinkedQueue<>();
    private Set<String> resetPasswordUsers = new HashSet<>();

    private static EntityPlayerMP getPlayerByUsername(String name) {
        EntityPlayerMP ret;
        try {
            ret = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.stream()
                    .filter(p -> p.getGameProfile().getName().equals(name)).collect(Collectors.toList()).get(0);
        } catch (Throwable e) {
            return null;
        }
        return ret;
    }

    private PlayerLoginHandler() {
        PLAYER_HANDLER_THREAD = new Thread(() -> {
            while (alive) {
                while (!tasks.isEmpty()) {
                    tasks.poll().run();
                }

                for (Login login : loginList) {
                    EntityPlayerMP player = getPlayerByUsername(login.name);
                    if (player == null) {
                        loginList.remove(login);
                        return;
                    }

                    if (System.currentTimeMillis() - login.time >= SLConfig.server.secs * 1000) {
                        player.playerNetServerHandler.kickPlayerFromServer("Login timed out.");
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {

                }
            }
        });
        alive = true;
        PLAYER_HANDLER_THREAD.start();
    }

    public static PlayerLoginHandler instance() {
        if (INSTANCE == null) INSTANCE = new PlayerLoginHandler();
        return INSTANCE;
    }

    public void login(String id, String pwd) {
        loginList.removeIf((l) -> l.name.equals(id));
        EntityPlayerMP player = getPlayerByUsername(id);
        if (player == null) {
            return;
        }

        IPassword capability = player.getCapability(CapabilityLoader.CAPABILITY_PASSWORD, null);
        if (capability == null) {
            SimpleLogin.logger.warn("Fail to load capability for player " + id + ". Ignoring...");
            return;
        }

        if (pwd.length() >= 100) {
            player.playerNetServerHandler.kickPlayerFromServer("Password too long.");
        } else if (capability.isFirst() || resetPasswordUsers.contains(id)) {
            capability.setFirst(false);
            capability.setPassword(pwd);
            setPlayerToSurvivalMode(player);
            resetPasswordUsers.remove(id);
            SimpleLogin.logger.info("Player " + id + " has successfully registered.");
        } else if (capability.getPassword().equals(pwd)) {
            setPlayerToSurvivalMode(player);
            SimpleLogin.logger.info("Player " + id + " has successfully logged in.");
        } else {
            player.playerNetServerHandler.kickPlayerFromServer("Wrong password.");
        }
    }

    private void setPlayerToSurvivalMode(EntityPlayerMP player) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> player.setGameType(WorldSettings.GameType.SURVIVAL));
    }

    void addPlayerToLoginList(EntityPlayerMP player) {
        loginList.add(new Login(player.getGameProfile().getName()));
        player.setGameType(WorldSettings.GameType.SPECTATOR);
    }

    boolean isPlayerInLoginList(String id) {
        return loginList.stream().anyMatch(e -> e.name.equals(id));
    }

    void resetPassword(String id) {
        resetPasswordUsers.add(id);
    }

    String getResetPasswordUsers() {
        StringBuilder ret = new StringBuilder();
        resetPasswordUsers.stream().map(i -> i + "\n").forEach(ret::append);
        return ret.toString();
    }

    private static class Login {
        String name;
        long time;

        Login(String name) {
            this.name = name;
            this.time = System.currentTimeMillis();
        }
    }

    void stop() {
        alive = false;
        try {
            PLAYER_HANDLER_THREAD.join();
        } catch (InterruptedException e) {
            SimpleLogin.logger.warn("Fail to shutdown login handler. ", e);
        }
    }
}
