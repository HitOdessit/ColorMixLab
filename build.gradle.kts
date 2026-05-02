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

// Capture catalog values at root scope; they are not accessible by name
// inside allprojects { } because the receiver is a subproject Project.
val detektVersion = libs.versions.detekt.get()
val ktlintVersion = libs.versions.ktlint.get()

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "com.diffplug.spotless")

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        toolVersion = detektVersion
        config.setFrom("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        autoCorrect = false
        // Don't fail `build` on lint findings. CI's quality job runs detekt
        // explicitly with continue-on-error, treating it as advisory.
        ignoreFailures = true
    }

    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        // Don't make `check` (and therefore `build`) depend on spotlessCheck.
        // Run `./gradlew spotlessCheck` explicitly when needed; CI's quality
        // job runs it with continue-on-error.
        isEnforceCheck = false

        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**", "**/.gradle/**")
            ktlint(ktlintVersion)
                .editorConfigOverride(
                    mapOf(
                        "ktlint_standard_filename" to "disabled",
                        "ktlint_standard_no-wildcard-imports" to "disabled",
                        "ktlint_standard_property-naming" to "disabled",
                        "ktlint_standard_function-naming" to "disabled",
                        "ktlint_standard_value-parameter-comment" to "disabled",
                        "ktlint_standard_value-argument-comment" to "disabled"
                    )
                )
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            targetExclude("**/build/**")
            ktlint(ktlintVersion)
        }
    }
}

dependencies {
    kover(project(":app"))
    kover(project(":shared"))
}
