package dev.arakiel.fargosoulfabric.common;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public final class Attachments {

    /** A value that has to know the target it is attached to. */
    public interface Bound {
        void bind(AttachmentTarget target);
    }

    private Attachments() {
    }

    public static <T> T get(AttachmentTarget target, AttachmentType<T> type) {
        T value = target.getAttachedOrCreate(type);
        if (value instanceof Bound bound) {
            bound.bind(target);
        }
        return value;
    }

    public static <T> T getOrNull(Object holder, AttachmentType<T> type) {
        return holder instanceof AttachmentTarget target ? target.getAttached(type) : null;
    }

    public static <T> void sync(Object holder, AttachmentType<T> type) {
        if (!(holder instanceof AttachmentTarget target)) {
            return;
        }
        T value = target.getAttached(type);
        if (value != null) {
            target.setAttached(type, value);
        }
    }
}
