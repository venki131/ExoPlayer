package com.example.exovideoplayer.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.Test

class CleanArchitectureKonsistTest {
    @Test
    fun `clean architecture layers have correct dependencies`() {
        val packageName = "com.example.exovideoplayer"
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                // Define layers
                val domain = Layer("domain", "$packageName.domain..")
                val presentation = Layer("ui", "$packageName.ui..")
                val data = Layer("data", "$packageName.data..")

                // Define architecture assertions
                domain.dependsOnNothing()
                presentation.dependsOn(domain)
                data.dependsOn(domain)
            }
    }
}