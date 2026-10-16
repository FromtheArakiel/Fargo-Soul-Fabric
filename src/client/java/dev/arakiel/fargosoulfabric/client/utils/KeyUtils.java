package dev.arakiel.fargosoulfabric.client.utils;

import net.minecraft.client.player.Input;

public class KeyUtils {
    
    private static Movement zza = Movement.NONE;
    private static int sprintingTime = 0;
    private static boolean zzKeyDown = false;

    private enum Movement {
        NONE,
        UP
    }

    public static boolean isDoubleTappingForward(Input input) {
        if (sprintingTime > 0) {
            sprintingTime--;
        }
        if (zza == Movement.NONE) {
            if (input.up) {
                zza = Movement.UP;
                sprintingTime = 7;
                zzKeyDown = true;
                return false;
            }
        } else if (zzKeyDown) {
            if (!input.up) {
                zzKeyDown = false;
            }
        } else if (sprintingTime > 0) {
            if (zza == Movement.UP && input.forwardImpulse >= 0.8) {
                zza = Movement.NONE;
                return true;
            }
        } else if (sprintingTime == 0) {
            zza = Movement.NONE;
        }
        return false;
    }
}
