package dev.anullihate.rankupmp.Gui;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.utils.ConfigSection;

public class RankSelectedButton extends ElementButton {

    private String group;
    private ConfigSection groupSection;

    public RankSelectedButton(String text, String group, ConfigSection groupSection) {
        super(text);
        this.group = group;
        this.groupSection = groupSection;
    }

    public String getGroup() {
        return group;
    }

    public ConfigSection getGroupSection() {
        return groupSection;
    }
}
