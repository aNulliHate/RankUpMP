package dev.anullihate.rankupmp.Gui;

import cn.nukkit.Player;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import dev.anullihate.rankupmp.RankUpMP;
import me.onebone.economyapi.EconomyAPI;
import ru.nukkit.multipass.Multipass;

import java.util.List;

public class RankSelectionGui extends FormWindowSimple implements Gui {

    public RankSelectionGui(Player player) {
        super("RankUp", "");

        Config configGroups = RankUpMP.configGroups;

        String playerGroup = Multipass.getGroup(player);

        if (configGroups.exists(playerGroup)) {
            ConfigSection groupSectionMP = configGroups.getSection(playerGroup);
            StringBuilder sbSetContent = new StringBuilder();

            configGroups.getSections().forEach((group, groupSection) -> {
                ConfigSection gs = (ConfigSection)groupSection;

                StringBuilder stringBuilder = new StringBuilder();

                if (!playerGroup.equals(group)) {
                    stringBuilder.append(String.format("&3&o&l%s&r", group)).append("\n");

                    if (groupSectionMP.getInt("tier") <= gs.getInt("tier")) {
                        stringBuilder.append(String.format("Tier: &a%d&r", gs.getInt("tier")));
                    }
                    stringBuilder.append(" | ");
                    if (EconomyAPI.getInstance().myMoney(player) <= gs.getInt("rankup")) {
                        stringBuilder.append(String.format("Cost: &c%d&r", gs.getInt("rankup")));
                    } else {
                        stringBuilder.append(String.format("Cost: &a%d&r", gs.getInt("rankup")));
                    }

                    if (groupSectionMP.getInt("tier") <= gs.getInt("tier")) {
                        addButton(new RankSelectedButton(TextFormat.colorize(stringBuilder.toString()), group, (ConfigSection) groupSection));
                    }
                } else {
                    sbSetContent.append(String.format("Current Rank: &c&o%s&r Tier: &e%d", group, gs.getInt("tier")));
                }
            });
            sbSetContent.append("\n");
            sbSetContent.append("\n");
            sbSetContent.append("&dAvailable Upgrades&r:");
            if (getButtons().size() == 0) {
                sbSetContent.append(" None");
            }
            setContent(TextFormat.colorize(sbSetContent.toString()));
        }
    }

    @Override
    public void onPlayerFormResponse(PlayerFormRespondedEvent event) {
        if (!(getResponse().getClickedButton() instanceof RankSelectedButton)) return;

        Player player = event.getPlayer();
        RankUpMP plugin = RankUpMP.getInstance();

        RankSelectedButton rankSelectedButton = (RankSelectedButton)getResponse().getClickedButton();
        String selectedGroup = rankSelectedButton.getGroup();
        ConfigSection selectedGroupSection = rankSelectedButton.getGroupSection();
        int result = EconomyAPI.getInstance().reduceMoney(player.getName(), (double) selectedGroupSection.getInt("rankup"));

        switch (result) {
            case EconomyAPI.RET_NO_ACCOUNT:
                break;
            case EconomyAPI.RET_CANCELLED:
            case EconomyAPI.RET_INVALID:
                player.sendMessage("failed to rank up!");
                break;
            case EconomyAPI.RET_SUCCESS:
                player.sendMessage("rank up successful!");
                plugin.getServer().dispatchCommand(new ConsoleCommandSender(),
                        String.format("user %s setgroup %s", player.getName(), selectedGroup)
                        );

                if (selectedGroupSection.containsKey("commands")) {
                    for (String commandLine : selectedGroupSection.getStringList("commands")) {
                        plugin.getServer().dispatchCommand(new ConsoleCommandSender(),
                                commandLine.replace("{player}", player.getName())
                        );
                    }
                }
                break;
        }
    }
}
