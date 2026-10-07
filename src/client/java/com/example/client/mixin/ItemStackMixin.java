package com.example.client.mixin;

import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {

    @Shadow public abstract Item getItem();

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void dynamicallyEvaluateName(CallbackInfoReturnable<Text> cir) {
        Text customName = this.get(DataComponentTypes.CUSTOM_NAME);
        if (customName != null) {
            return;
        }

        // Bypass ITEM_NAME data component since it caches translations
        cir.setReturnValue(this.getItem().getName((ItemStack) (Object) this));
    }
}
