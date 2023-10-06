package com.example.exovideoplayer.konsist

import androidx.lifecycle.ViewModel
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withParentClassOf
import com.lemonappdev.konsist.api.verify.assert
import org.junit.Test

class AndroidKonsistTest {
    @Test
    fun `classes extending 'ViewModel' ends with 'ViewModel' suffix`() {
        Konsist.scopeFromProject()
            .classes()
            .withParentClassOf(ViewModel::class)
            .assert { it.name.endsWith("ViewModel", true) }
    }
}