package com.example.client.mixin;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(I18n.class)
public interface I18nAccessor {
    @Invoker("setLanguage")
    static void setLanguage(Language language) {
        throw new AssertionError();
    }
}
