package dev.arakiel.fargosoulfabric.common.item.base;

import java.util.List;

public record ValueModifier(float amount, ValueOperation valueOperation) {

    public static float getModifierAfter(float amount, List<ValueModifier> modifiers) {
        float newDamage = amount;
        for (ValueModifier modifier : modifiers) {
            if (modifier.valueOperation() == ValueOperation.ADD_VALUE) {
                newDamage += modifier.amount();
            }
        }
        float scale = 0;
        for (ValueModifier modifier : modifiers) {
            if (modifier.valueOperation() == ValueOperation.ADD_MULTIPLIED_BASE) {
                scale += modifier.amount();
            }
        }
        newDamage += newDamage * scale;
        for (ValueModifier modifier : modifiers) {
            if (modifier.valueOperation() == ValueOperation.ADD_MULTIPLIED_TOTAL) {
                newDamage *= 1 + modifier.amount();
            }
        }
        return newDamage;
    }
}
