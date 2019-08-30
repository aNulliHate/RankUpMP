package dev.anullihate.rankupmp;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import dev.anullihate.rankupmp.Gui.Gui;
import dev.anullihate.rankupmp.Gui.RankSelectionGui;
import ru.nukkit.multipass.MultipassPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RankUpMP extends PluginBase implements Listener {

    private static RankUpMP plugin;

    private MultipassPlugin multipassPlugin;
    private Config multipassGroupsConfig;

    public static Config configGroups;

    private void loadGroups() {
        this.saveResource("groups.yml", false);

        configGroups = new Config(getDataFolder() + "/groups.yml", Config.YAML);

        multipassGroupsConfig.getSections().forEach((group, groupSection) -> {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            ConfigSection multipassGroupSection = (ConfigSection)groupSection;

            if (multipassGroupSection.getBoolean("rankup")) {
                if (!configGroups.exists(group)) {
                    map.put("tier", 0);
                    map.put("rankup", 0);
                    map.put("commands", new ArrayList<>());
                    configGroups.set(group, new ConfigSection(map));
                }
            }
        });

        configGroups.save();
    }

    public static RankUpMP getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        //
        if (getServer().getPluginManager().getPlugin("Multipass") == null) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        multipassPlugin = MultipassPlugin.getPlugin();
        multipassGroupsConfig = new Config(new File(multipassPlugin.getDataFolder(), "groups.yml"));

        loadGroups();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == this.getServer().getConsoleSender()) {
            sender.sendMessage("\u00A7cThis command only works in game");
            return true;
        }
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("rankup")) {
            if (args.length == 0) {
                player.showFormWindow(new RankSelectionGui(player));
                return true;
            }
            return false;
        }
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFormResponse(PlayerFormRespondedEvent event) {
        if (event.getResponse() == null) return;
        if (!(event.getWindow() instanceof Gui))return;

        ((Gui)event.getWindow()).onPlayerFormResponse(event);
    }
}
