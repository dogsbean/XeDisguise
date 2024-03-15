package io.dogsbean.xedisguise.disguise;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter @Setter
public class Disguise {
    private Player player;
    private String originalName;
    private String disguisedName;

    public Disguise(Player player, String originalName, String disguisedName) {
        this.player = player;
        this.originalName = originalName;
        this.disguisedName = disguisedName;
    }

}
