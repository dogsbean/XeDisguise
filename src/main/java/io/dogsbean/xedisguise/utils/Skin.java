package io.dogsbean.xedisguise.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Credit: github.com/AndyReckt/Holiday
 * Author: Creaxx, AndyReckt
 */

@Getter @Setter
@AllArgsConstructor
public class Skin {
    public static final String ID_API = "https://api.mojang.com/users/profiles/minecraft/";
    public static final String WEB_API = "https://sessionserver.mojang.com/session/minecraft/profile/";
    public static final Map<String, Skin> SKINS = new HashMap<>();

    public static Skin DEFAULT_SKIN = new Skin("", "", "");

    private String name;
    private String value;
    private String signature;

    public static CompletableFuture<Skin> getSkinByName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            if (SKINS.containsKey(name)) {
                return SKINS.get(name);
            }

            String texture = null;
            String signature = null;

            try {
                String idChecker = ID_API + name;
                URL urlChecker = new URL(idChecker);

                InputStreamReader reader = new InputStreamReader(urlChecker.openStream());
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(reader);
                JsonObject object = element.getAsJsonObject();
                String id = object.get("id").getAsString();

                String link = WEB_API + id + "?unsigned=false";
                urlChecker = new URL(link);
                reader = new InputStreamReader(urlChecker.openStream());
                element = parser.parse(reader);
                JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

                texture = properties.get("value").getAsString();
                signature = properties.get("signature").getAsString();
                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                return Skin.DEFAULT_SKIN;
            }
            Skin skin = new Skin(name, texture, signature);
            SKINS.put(name, skin);
            return skin;
        });
    }
}
