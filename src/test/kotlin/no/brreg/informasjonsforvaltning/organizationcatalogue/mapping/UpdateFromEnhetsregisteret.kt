package no.brreg.informasjonsforvaltning.organizationcatalogue.mapping

import no.brreg.informasjonsforvaltning.organizationcatalogue.model.PrefLabel
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.BRREG_ORG
import no.brreg.informasjonsforvaltning.organizationcatalogue.utils.orgDB1
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@Tag("unit")
@ActiveProfiles("test")
class UpdateFromEnhetsregisteret {

    @Test
    fun prefLabelNotUpdatedWhenNameIsUnchanged() {
        val orgWithNN = orgDB1().apply {
            prefLabel = PrefLabel(
                nb = "Forsvaret på bokmål",
                nn = "Forsvaret på nynorsk"
            )
        }

        val expectedNb = "Forsvaret på bokmål"
        val expectedNn = "Forsvaret på nynorsk"

        val result = orgWithNN.updateWithEnhetsregisteretValues(BRREG_ORG)

        Assertions.assertEquals(expectedNb, result.prefLabel.nb)
        Assertions.assertEquals(expectedNn, result.prefLabel.nn)
        Assertions.assertNull(result.prefLabel.en)
    }

    @Test
    fun prefLabelNotUpdatedWhenNameIsBlank() {
        val orgWithNN = orgDB1().apply {
            prefLabel = PrefLabel(
                nb = "Forsvaret på bokmål",
                nn = "Forsvaret på nynorsk"
            )
        }

        val expectedNb = "Forsvaret på bokmål"
        val expectedNn = "Forsvaret på nynorsk"

        val result = orgWithNN.updateWithEnhetsregisteretValues(BRREG_ORG.copy(navn = "  "))

        Assertions.assertEquals(expectedNb, result.prefLabel.nb)
        Assertions.assertEquals(expectedNn, result.prefLabel.nn)
        Assertions.assertNull(result.prefLabel.en)
    }

    @Test
    fun prefLabelUpdatedWhenNameIsChanged() {
        val orgDBWithTypo = orgDB1().apply {
            name = "FØRSVARET"
            prefLabel = PrefLabel(
                nb = "Førsvaret",
                nn = "Føresvaret"
            )
        }

        val expectedNb = "Forsvaret"

        val result = orgDBWithTypo.updateWithEnhetsregisteretValues(BRREG_ORG)

        Assertions.assertEquals(expectedNb, result.prefLabel.nb)
        Assertions.assertNull(result.prefLabel.nn)
        Assertions.assertNull(result.prefLabel.en)
    }

    @Test
    fun prefLabelUpdatedWhenPrefLabelIsEmpty() {
        val orgDBWithTypo = orgDB1().apply {
            prefLabel = PrefLabel()
        }

        val expectedNb = "Forsvaret"

        val result = orgDBWithTypo.updateWithEnhetsregisteretValues(BRREG_ORG)

        Assertions.assertEquals(expectedNb, result.prefLabel.nb)
        Assertions.assertNull(result.prefLabel.nn)
        Assertions.assertNull(result.prefLabel.en)
    }

}
