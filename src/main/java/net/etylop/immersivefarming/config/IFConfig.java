package net.etylop.immersivefarming.config;

import net.jodah.typetools.TypeResolver;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class IFConfig {
    public static Common get() {
        return Holder.COMMON;
    }

    public static ForgeConfigSpec spec() {
        return Holder.COMMON_SPEC;
    }

    private static final class Holder {
        private static final Common COMMON;

        private static final ForgeConfigSpec COMMON_SPEC;

        static {
            final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
            COMMON = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
    }

    public static class Common {
        public final CartConfig plow;
        public final CropConfig cropConfig;

        Common(final ForgeConfigSpec.Builder builder) {
            this.cropConfig = new CropConfig(builder);
            builder.comment("Configuration for all carts and cart-like vehicles\n\nDefault pull_animals = " + referencePullAnimals()).push("carts");
            this.plow = new CartConfig(builder, "plow", "The Plow, an animal pulled machine for tilling soil");
            builder.pop();
        }

        static String referencePullAnimals() {
            return "[\n" +
                StreamSupport.stream(ForgeRegistries.ENTITIES.spliterator(), false)
                    .filter(type -> {
                        final Class<?> entityClass = TypeResolver.resolveRawArgument(EntityType.EntityFactory.class, Objects.requireNonNull(
                            ObfuscationReflectionHelper.getPrivateValue(EntityType.class, type, "f_20535_"),
                            "factory"
                        ).getClass());
                        return Saddleable.class.isAssignableFrom(entityClass) &&
                            !ItemSteerable.class.isAssignableFrom(entityClass) &&
                            !Llama.class.isAssignableFrom(entityClass); // no horse-llamas
                    })
                    .map(ForgeRegistryEntry::getRegistryName)
                    .filter(Objects::nonNull)
                    .map(type -> "    \"" + type.toString() + "\"")
                    .collect(Collectors.joining(",\n")) +
                "\n  ]";
        }
    }

    public static class CartConfig {
        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> pullAnimals;
        public final ForgeConfigSpec.DoubleValue slowSpeed;
        public final ForgeConfigSpec.DoubleValue pullSpeed;

        CartConfig(final ForgeConfigSpec.Builder builder, final String name, final String description) {
            builder.comment(description).push(name);
            this.pullAnimals = builder
                .comment(
                    "Animals that are able to pull this cart, such as [\"minecraft:horse\"]\n" +
                    "An empty list defaults to all which may wear a saddle but not steered by an item"
                )
                .define("pull_animals", new ArrayList<>());
            this.slowSpeed = builder.comment("Slow speed modifier toggled by the sprint key")
                .defineInRange("slow_speed", -0.0D, -1.0D, 0.0D);
            this.pullSpeed = builder.comment("Base speed modifier applied to animals (-0.5 = half normal speed)")
                .defineInRange("pull_speed", -0.5D, -1.0D, 0.0D);
            builder.pop();
        }
    }

    public static class CropConfig {
        public final ForgeConfigSpec.DoubleValue start_contamination;
        public final ForgeConfigSpec.DoubleValue proximity_contamination;
        public final ForgeConfigSpec.DoubleValue lethality_contamination;

        CropConfig(final ForgeConfigSpec.Builder builder) {
            this.start_contamination = builder.comment("Probability for a crop to become sick during a random tick")
                    .defineInRange("start_contamination", 0.0001D, 0D, 0.0D);
            this.proximity_contamination = builder.comment("Probability for a crop to become sick during a random tick")
                    .defineInRange("proximity_contamination", 0.1D, 0D, 0.0D);
            this.lethality_contamination = builder.comment("Probability for a crop to become sick during a random tick")
                    .defineInRange("lethality_contamination", 0.02D, 0D, 0.0D);
        }
    }
}
