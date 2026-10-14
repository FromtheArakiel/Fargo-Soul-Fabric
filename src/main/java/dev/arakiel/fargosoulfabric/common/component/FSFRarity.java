package dev.arakiel.fargosoulfabric.common.component;

import dev.arakiel.fargosoulfabric.register.FSFRegisters;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record FSFRarity(int color) {
   public static final FSFRarity Gray = new FSFRarity(8553090);
   public static final FSFRarity White = new FSFRarity(16777215);
   public static final FSFRarity Blue = new FSFRarity(9869055);
   public static final FSFRarity Green = new FSFRarity(9895830);
   public static final FSFRarity Orange = new FSFRarity(16763030);
   public static final FSFRarity LightRed = new FSFRarity(16750230);
   public static final FSFRarity Pink = new FSFRarity(16750335);
   public static final FSFRarity LightPurple = new FSFRarity(13803775);
   public static final FSFRarity Lime = new FSFRarity(9895690);
   public static final FSFRarity Yellow = new FSFRarity(16776970);
   public static final FSFRarity Cyan = new FSFRarity(379135);
   public static final FSFRarity Red = new FSFRarity(16722020);
   public static final FSFRarity Purple = new FSFRarity(11806975);
   public static final FSFRarity Rainbow = new FSFRarity(-1);
   public static final Map<FSFRarity, Function<Float, Integer>> DynamicColorData = new HashMap<>();
   public static final Codec<FSFRarity> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.INT.fieldOf("color").forGetter(fsfRarity -> fsfRarity.color)).apply(instance, FSFRarity::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, FSFRarity> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

   public FSFRarity(int color) {
      this.color = color;
   }

   public int color() {
      Function<Float, Integer> function = DynamicColorData.get(this);
      if (function != null) {
         Double time = RarityAnimation.currentTime();
         if (time != null) {
            return function.apply(time.floatValue());
         }
      }

      return this.color;
   }

   public static Component handler(ItemStack stack, MutableComponent component) {
      FSFRarity fsfRarity = stack.get(FSFRegisters.RARITY);
      return fsfRarity != null ? component.withColor(fsfRarity.color()) : component;
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof FSFRarity other ? this.color == other.color : false;
   }

   static {
      DynamicColorData.put(Rainbow, time -> {
         float hue = time * 0.005F % 1.0F;
         hue = (float)((double)hue - Math.floor((double)hue));
         int sector = (int)(hue * 6.0F);
         float f = hue * 6.0F - (float)sector;
         float p = 0.0F;
         float q = 1.0F - f;
         float t = 1.0F - (1.0F - f);
         float r;
         float g;
         float b;
         switch (sector % 6) {
            case 0:
               r = 1.0F;
               g = t;
               b = p;
               break;
            case 1:
               r = q;
               g = 1.0F;
               b = p;
               break;
            case 2:
               r = p;
               g = 1.0F;
               b = t;
               break;
            case 3:
               r = p;
               g = q;
               b = 1.0F;
               break;
            case 4:
               r = t;
               g = p;
               b = 1.0F;
               break;
            default:
               r = 1.0F;
               g = p;
               b = q;
         }

         return (int)(r * 255.0F) << 16 | (int)(g * 255.0F) << 8 | (int)(b * 255.0F);
      });
   }
}