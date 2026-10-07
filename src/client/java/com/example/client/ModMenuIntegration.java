package com.example.client;

import com.example.client.config.ModConfig;
import com.example.client.config.SwitchMode;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("Instant Language Switcher Config"));

            builder.setSavingRunnable(() -> {
                ModConfig.save();
                ExampleModClient.reloadSecondaryLanguage();
            });

            ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startStrField(Text.literal("Secondary Language"), ModConfig.secondaryLanguage)
                    .setDefaultValue("en_us")
                    .setSaveConsumer(newValue -> ModConfig.secondaryLanguage = newValue)
                    .build());

            general.addEntry(entryBuilder.startEnumSelector(Text.literal("Switch Mode"), SwitchMode.class, ModConfig.switchMode)
                    .setDefaultValue(SwitchMode.HOLD)
                    .setSaveConsumer(newValue -> ModConfig.switchMode = newValue)
                    .build());

            return builder.build();
        };
    }
}
