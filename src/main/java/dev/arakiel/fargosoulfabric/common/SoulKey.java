package dev.arakiel.fargosoulfabric.common;

public enum SoulKey {

    /** The key that lights the blaze soul up. */
    Blaze("日耀"),

    /** The key that turns the gold soul into a stasis field. */
    Gold("金身"),

    /** The key that freezes the stardust soul. */
    Stardust("冻结"),

    /** The key that teleports with the vortex soul. */
    Vortex("传送"),

    /** The key that calls the forbidden storm. */
    Forbidden("风暴"),

    /** The key that arms the penetrating sprint. */
    PenetratingNinja("渗透"),

    /** The key that makes the palm wood soul explode. */
    PalmWood("棕榈爆炸"),

    /** The key that opens the soul slot menu. */
    SoulMenu("打开魂石空间");

    private final String displayName;

    SoulKey(String displayName) {
        this.displayName = displayName;
    }

    /** The name the key is listed under in the controls screen. */
    public String displayName() {
        return this.displayName;
    }
}
