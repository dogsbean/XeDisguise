package io.dogsbean.xedisguise.disguise;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DisguiseManager {
    private final Map<Player, Disguise> disguiseMap;

    public DisguiseManager() {
        this.disguiseMap = new HashMap<>();
    }

    public void addDisguise(Player player, Disguise disguise) {
        disguiseMap.put(player, disguise);
    }

    public void removeDisguise(Player player) {
        disguiseMap.remove(player);
    }

    public Disguise getDisguise(Player player) {
        return disguiseMap.get(player);
    }

    public boolean isDisguised(Player player) {
        return getDisguise(player) != null;
    }

    public Set<String> getAllOriginalNames() {
        Set<String> originalNames = new HashSet<>();
        for (Disguise disguise : disguiseMap.values()) {
            originalNames.add(disguise.getOriginalName());
        }
        return originalNames;
    }
}
