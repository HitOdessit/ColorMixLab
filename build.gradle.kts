plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover)
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "com.diffplug.spotless")

    detekt {
        toolVersion = libs.versions.detekt.get()
        config.setFrom("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        autoCorrect = false
    }

    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**", "**/.gradle/**")
            ktlint(libs.versions.ktlint.get())
                .editorConfigOverride(
                    mapOf(
                        "ktlint_standard_filename" to "disabled",
                        "ktlint_standard_no-wildcard-imports" to "disabled",
                        "ktlint_standard_property-naming" to "disabled",
                        "ktlint_standard_function-naming" to "disabled"
                    )
                )
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            targetExclude("**/build/**")
            ktlint(libs.versions.ktlint.get())
        }
    }
}

dependencies {
    kover(project(":app"))
    kover(project(":shared"))
}
