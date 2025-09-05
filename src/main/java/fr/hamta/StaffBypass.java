package fr.hamta;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffBypass extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (e.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            if (e.getPlayer().hasPermission("staffbypass.joinfull")) {
                e.allow();
            }
        }
    }
}
