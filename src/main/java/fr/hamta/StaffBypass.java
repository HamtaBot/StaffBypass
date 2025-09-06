package fr.hamta;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StaffBypass extends JavaPlugin implements Listener {

    private Set<UUID> bypassList = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadBypassList();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadBypassList() {
        FileConfiguration config = getConfig();
        bypassList.clear();
        List<String> uuids = config.getStringList("bypass-uuids");
        for (String uuidStr : uuids) {
            try {
                bypassList.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException ex) {
                getLogger().warning("UUID invalide dans la config: " + uuidStr);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (getServer().getOnlinePlayers().size() >= getServer().getMaxPlayers()) {
            if (bypassList.contains(e.getUniqueId())) {
                e.allow();
            } else {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "Le serveur est plein !");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cSeuls les opérateurs peuvent utiliser cette commande.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§eUtilisation: /staffbypass <add|reload> [uuid]");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            loadBypassList();
            sender.sendMessage("§aConfiguration rechargée !");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                sender.sendMessage("§cUtilisation: /staffbypass add <uuid>");
                return true;
            }
            try {
                UUID uuid = UUID.fromString(args[1]);
                List<String> uuids = getConfig().getStringList("bypass-uuids");
                if (!uuids.contains(uuid.toString())) {
                    uuids.add(uuid.toString());
                    getConfig().set("bypass-uuids", uuids);
                    saveConfig();
                    loadBypassList();
                    sender.sendMessage("§aUUID ajouté : " + uuid);
                } else {
                    sender.sendMessage("§eCet UUID est déjà dans la liste.");
                }
            } catch (IllegalArgumentException ex) {
                sender.sendMessage("§cUUID invalide.");
            }
            return true;
        }

        sender.sendMessage("§eUtilisation: /staffbypass <add|reload> [uuid]");
        return true;
    }
}
