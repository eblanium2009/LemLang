package com.example.client;

import com.example.client.config.ModConfig;
import com.example.client.config.SwitchMode;
import com.example.client.mixin.LanguageManagerAccessor;
import com.example.client.mixin.I18nAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Language;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExampleModClient implements ClientModInitializer {
    private static KeyBinding languageSwitchKey;
    public static boolean isSecondaryActive = false;

    public static TranslationStorage primaryLanguageStorage;
    public static TranslationStorage secondaryLanguageStorage;

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        languageSwitchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.instant_language_switcher.switch",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.instant_language_switcher.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModConfig.switchMode == SwitchMode.HOLD) {
                boolean isHeld = languageSwitchKey.isPressed();
                if (isHeld && !isSecondaryActive) {
                    isSecondaryActive = true;
                    updateActiveLanguage(client);
                } else if (!isHeld && isSecondaryActive) {
                    isSecondaryActive = false;
                    updateActiveLanguage(client);
                }
            } else if (ModConfig.switchMode == SwitchMode.TOGGLE) {
                while (languageSwitchKey.wasPressed()) {
                    isSecondaryActive = !isSecondaryActive;
                    updateActiveLanguage(client);
                }
            }
        });
    }

    public static void updateActiveLanguage(MinecraftClient client) {
        TranslationStorage active = isSecondaryActive && secondaryLanguageStorage != null ? secondaryLanguageStorage : primaryLanguageStorage;
        if (active != null) {
            Language.setInstance(active);
            I18nAccessor.setLanguage(active);

            if (client != null && client.currentScreen != null) {
                // Force re-initialization of the active screen without closing it
                client.currentScreen.resize(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
            }
        }
    }

    public static void updateActiveLanguage() {
        updateActiveLanguage(MinecraftClient.getInstance());
    }

    public static void reloadSecondaryLanguage() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getResourceManager() == null || client.getLanguageManager() == null) {
            return;
        }

        List<String> list = new ArrayList<>();
        list.add("en_us");
        boolean rightToLeft = false;

        if (!ModConfig.secondaryLanguage.equals("en_us")) {
            list.add(ModConfig.secondaryLanguage);

            LanguageManagerAccessor accessor = (LanguageManagerAccessor) client.getLanguageManager();
            Map<String, LanguageDefinition> defs = accessor.getLanguageDefs();
            LanguageDefinition def = defs.get(ModConfig.secondaryLanguage);
            if (def != null) {
                rightToLeft = def.rightToLeft();
            }
        }

        secondaryLanguageStorage = TranslationStorage.load(client.getResourceManager(), list, rightToLeft);
        updateActiveLanguage();
    }
}
