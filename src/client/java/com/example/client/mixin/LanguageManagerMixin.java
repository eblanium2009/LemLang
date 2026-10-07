package com.example.client.mixin;

import com.example.client.ExampleModClient;
import com.example.client.config.ModConfig;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(LanguageManager.class)
public abstract class LanguageManagerMixin {

    @Shadow private Map<String, LanguageDefinition> languageDefs;

    @Inject(method = "reload", at = @At("TAIL"))
    private void onReload(ResourceManager manager, CallbackInfo ci) {
        Language primary = Language.getInstance();
        if (!(primary instanceof TranslationStorage)) {
            return;
        }

        List<String> list = new ArrayList<>();
        list.add("en_us");
        boolean rightToLeft = false;

        if (!ModConfig.secondaryLanguage.equals("en_us")) {
            list.add(ModConfig.secondaryLanguage);

            LanguageDefinition def = this.languageDefs.get(ModConfig.secondaryLanguage);
            if (def != null) {
                rightToLeft = def.rightToLeft();
            }
        }

        TranslationStorage secondary = TranslationStorage.load(manager, list, rightToLeft);

        ExampleModClient.primaryLanguageStorage = (TranslationStorage) primary;
        ExampleModClient.secondaryLanguageStorage = secondary;

        ExampleModClient.updateActiveLanguage();
    }
}
