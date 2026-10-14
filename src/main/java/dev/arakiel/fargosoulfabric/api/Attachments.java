package dev.arakiel.fargosoulfabric.api;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

/**
 * Helpers around the Fabric data attachment API.
 *
 * <p>Fabric sends an attachment to the clients that track the holder as soon as it is written
 * again, so re-setting the value is all a {@code sync} call has to do.
 */
public final class Attachments {

    /** A value that has to know the target it is attached to. */
    public interface Bound {
        void bind(AttachmentTarget target);
    }

    private Attachments() {
    }

    /**
     * The value of {@code type} on {@code target}.
     *
     * <p>The value is created on first access by the initializer of the attachment type; values that
     * need to know their target bind themselves here.
     */
    public static <T> T get(AttachmentTarget target, AttachmentType<T> type) {
        T value = target.getAttachedOrCreate(type);
        if (value instanceof Bound bound) {
            bound.bind(target);
        }
        return value;
    }

    /**
     * The value of {@code type} on {@code holder}, or {@code null} when it was never written.
     *
     * <p>Unlike {@link #get(AttachmentTarget, AttachmentType)} this never creates the value, so it is the
     * one to use when looping over entities that may not carry the attachment at all.
     */
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
