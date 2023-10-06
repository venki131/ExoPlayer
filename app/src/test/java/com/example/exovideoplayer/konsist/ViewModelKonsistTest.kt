package com.example.exovideoplayer.konsist

import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withoutAllModifiers
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assert
import org.junit.Test
import java.util.Locale

class ViewModelKonsistTest {

    @Test
    fun `every view model constructor parameter has name derived from parameter type`() {
        Konsist.scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .withoutAllModifiers(KoModifier.ABSTRACT)
            .flatMap { it.constructors }
            /*.flatMap { it.constructors }
            .flatMap { it.parameters }
            .assert {
                val nameTitleCase = it.name.replaceFirstChar { char -> char.titlecase(Locale.getDefault()) }
                nameTitleCase == it.type.sourceType
            }*/
    }

    @Test
    fun `rest`() {
        /*Konsist
            .scopeFromPackage("com.example.exovideoplayer.data.repository..")
            .classes()
            .assert { it.hasTest() }*/
        Konsist.scopeFromProject("app")
            .classes()
            .withPackage("com.example.exovideoplayer.data..")
            .assert { it.hasTest() }
    }
}