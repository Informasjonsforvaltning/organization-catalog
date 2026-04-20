package no.digdir.organizationcatalog.mapping

import no.digdir.organizationcatalog.model.EmbeddedPrefLabel
import no.digdir.organizationcatalog.model.toPrefLabel
import no.digdir.organizationcatalog.utils.BRREG_ORG
import no.digdir.organizationcatalog.utils.ORG_DB1
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@Tag("unit")
@ActiveProfiles("test")
class UpdateFromEnhetsregisteret {
    @Test
    fun prefLabelNotUpdatedWhenNameIsUnchanged() {
        val orgWithNN =
            ORG_DB1.copy(
                prefLabel =
                    EmbeddedPrefLabel(
                        nb = "Forsvaret på bokmål",
                        nn = "Forsvaret på nynorsk",
                    ),
            )

        val expectedNb = "Forsvaret på bokmål"
        val expectedNn = "Forsvaret på nynorsk"

        val result = orgWithNN.updateWithEnhetsregisteretValues(BRREG_ORG)

        Assertions.assertEquals(expectedNb, result.prefLabel?.toPrefLabel()?.nb)
        Assertions.assertEquals(expectedNn, result.prefLabel?.toPrefLabel()?.nn)
        Assertions.assertNull(result.prefLabel?.toPrefLabel()?.en)
    }

    @Test
    fun prefLabelNotUpdatedWhenNameIsBlank() {
        val orgWithNN =
            ORG_DB1.copy(
                prefLabel =
                    EmbeddedPrefLabel(
                        nb = "Forsvaret på bokmål",
                        nn = "Forsvaret på nynorsk",
                    ),
            )

        val expectedNb = "Forsvaret på bokmål"
        val expectedNn = "Forsvaret på nynorsk"

        val result = orgWithNN.updateWithEnhetsregisteretValues(BRREG_ORG.copy(navn = "  "))

        Assertions.assertEquals(expectedNb, result.prefLabel?.toPrefLabel()?.nb)
        Assertions.assertEquals(expectedNn, result.prefLabel?.toPrefLabel()?.nn)
        Assertions.assertNull(result.prefLabel?.toPrefLabel()?.en)
    }

    @Test
    fun prefLabelUpdatedWhenNameIsChanged() {
        val orgDBWithTypo =
            ORG_DB1.copy(
                name = "FØRSVARET",
                prefLabel =
                    EmbeddedPrefLabel(
                        nb = "Førsvaret",
                        nn = "Føresvaret",
                    ),
            )

        val expectedNb = "Forsvaret"

        val result = orgDBWithTypo.updateWithEnhetsregisteretValues(BRREG_ORG)

        Assertions.assertEquals(expectedNb, result.prefLabel?.toPrefLabel()?.nb)
        Assertions.assertNull(result.prefLabel?.toPrefLabel()?.nn)
        Assertions.assertNull(result.prefLabel?.toPrefLabel()?.en)
    }

    @Test
    fun prefLabelUpdatedWhenPrefLabelIsEmpty() {
        val orgDBWithTypo =
            ORG_DB1.copy(
                prefLabel = EmbeddedPrefLabel(),
            )

        val expectedNb = "Forsvaret"

        val result = orgDBWithTypo.updateWithEnhetsregisteretValues(BRREG_ORG)

        Assertions.assertEquals(expectedNb, result.prefLabel?.toPrefLabel()?.nb)
        Assertions.assertNull(result.prefLabel?.toPrefLabel()?.nn)
        Assertions.assertNull(result.prefLabel?.toPrefLabel()?.en)
    }
}
