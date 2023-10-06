package com.example.exovideoplayer.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.functions
import com.lemonappdev.konsist.api.verify.assertNot
import org.junit.Test

class TestKonsistTest {
    @Test
    fun `don't use JUnit4 Test annotation`() {
        Konsist.scopeFromProject()
            .classes()
            .functions()
            .assertNot { it.hasAnnotations("org.junit.Test") } // should be only org.junit.jupiter.api.Test
    }
}