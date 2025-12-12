plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-root")
    id("com.diffplug.spotless") version "8.1.0"
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    spotless {
        java {
            importOrder()
            endWithNewline()
            removeUnusedImports()
            formatAnnotations()
        }

        format("MountsOfMayhem") {
            target("src/main/java/**/*.java")

            replaceRegex(
                "Update @Nullable import",
                "\\bimport\\s+org\\.jetbrains\\.annotations\\.Nullable;",
                "import org.jspecify.annotations.Nullable"
            )

            replaceRegex(
                "Update @NotNull import",
                "\\bimport\\s+org\\.jetbrains\\.annotations\\.NotNull",
                "import org.jspecify.annotations.NonNull"
            )

            replaceRegex(
                "Change @NotNull to @NonNull",
                "\\b@NotNull\\b",
                "@NonNull"
            )

            replaceRegex(
                "Update Util import",
                "\\bimport\\s+net\\.minecraft\\.Util;",
                "import net.minecraft.util.Util;"
            )

            replaceRegex(
                "Update ResourceLocation import",
                "\\bimport\\s+net\\.minecraft\\.util\\.ResourceLocation;",
                "import net.minecraft.util.Identifier;"
            )

            replaceRegex(
                "Change ResourceLocation to Identifier",
                "\\bResourceLocation\\b",
                "Identifier"
            )

            replaceRegex(
                "Update ResourceLocation variables",
                "\\bresourceLocation\\b",
                "identifier"
            )

            replaceRegex(
                "Update ResourceLocation comments",
                "\\bresource location\\b",
                "identifier"
            )

            replaceRegex(
                "Update ResourceLocationHelper import",
                "\\bimport\\s+fuzs\\.puzzleslib\\.api\\.core\\.v1\\.utility\\.ResourceLocationHelper;",
                "import net.minecraft.util.Identifier;"
            )

            replaceRegex(
                "Change ResourceLocationHelper to Identifier",
                "\\bResourceLocationHelper\\b",
                "Identifier"
            )
        }
    }
}
