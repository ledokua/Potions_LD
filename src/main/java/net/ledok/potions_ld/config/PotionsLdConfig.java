package net.ledok.potions_ld.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.ledok.potions_ld.PotionsLdMod;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple JSON-backed config for the Alchemy Table upgrade modifiers.
 * Written to {@code config/potions_ld.json} on first launch.
 */
public class PotionsLdConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PotionsLdConfig INSTANCE = new PotionsLdConfig();

    /** Speed upgrade: cooking-time multiplier (0.7 = -30% time). Range (0, 1]. */
    public float speedTimeMultiplier = 0.7f;
    /** Efficiency upgrade: ingredient-cost multiplier (0.5 = -50% cost). Range (0, 1]. */
    public float efficiencyCostMultiplier = 0.5f;
    /** Fortune upgrade: chance (0..1) to produce bonus output. */
    public float fortuneChance = 0.1f;
    /** Fortune upgrade: number of bonus outputs when the chance triggers. */
    public int fortuneBonus = 1;

    public static PotionsLdConfig get() {
        return INSTANCE;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(PotionsLdMod.MOD_ID + ".json");
    }

    /** Loads the config from disk (or seeds defaults), clamps it, then writes it back. */
    public static void load() {
        Path path = path();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                PotionsLdConfig loaded = GSON.fromJson(reader, PotionsLdConfig.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                }
            } catch (Exception e) {
                PotionsLdMod.LOGGER.error("Failed to read config {}, using defaults", path, e);
            }
        }
        INSTANCE.clamp();
        save();
    }

    public static void save() {
        Path path = path();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            PotionsLdMod.LOGGER.error("Failed to write config {}", path, e);
        }
    }

    private void clamp() {
        speedTimeMultiplier = clampMultiplier(speedTimeMultiplier);
        efficiencyCostMultiplier = clampMultiplier(efficiencyCostMultiplier);
        fortuneChance = Math.max(0f, Math.min(1f, fortuneChance));
        fortuneBonus = Math.max(0, fortuneBonus);
    }

    /** Multipliers must stay positive and never make a craft slower/costlier than the base recipe. */
    private static float clampMultiplier(float value) {
        if (value <= 0f) {
            return 0.01f;
        }
        return Math.min(value, 1f);
    }
}
