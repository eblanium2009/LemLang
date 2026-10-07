package com.example.client.mixin;

import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TranslatableTextContent.class)
public abstract class TranslatableTextContentMixin {
    @Shadow private Language languageCache;

    @Inject(method = "updateTranslations", at = @At("HEAD"))
    private void clearLanguageCache(CallbackInfo ci) {
        if (this.languageCache != Language.getInstance()) {
            this.languageCache = null;
        }
    }
}
