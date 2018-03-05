package com.realgecko.borntobe;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
    static final Config instance = new Config();
    int chance;

    void load(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        chance = config.getInt(
                "chance",
                Configuration.CATEGORY_GENERAL,
                20,
                1,
                100,
                "Chance in % of minecraft:farmer to shift profession on spawn"
        ) - 1;
        if (config.hasChanged()) config.save();
    }
}
