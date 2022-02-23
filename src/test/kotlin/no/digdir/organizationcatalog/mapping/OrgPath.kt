package no.digdir.organizationcatalog.mapping

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@Tag("unit")
@ActiveProfiles("test")
class OrgPath {

    @Nested
    internal inner class CreateOrgPath {

        @Test
        fun baseOrgPath() {
            assertEquals("/STAT/123456789", createOrgPath(false, setOf("123456789"), "STAT"))
            assertEquals("/FYLKE/123456789", createOrgPath(false, setOf("123456789"), "FYLK"))
            assertEquals("/KOMMUNE/123456789", createOrgPath(false, setOf("123456789"), "KOMM"))
            assertEquals("/ANNET/123456789", createOrgPath(false, setOf("123456789"), "IKS"))
            assertEquals("/PRIVAT/123456789", createOrgPath(false, setOf("123456789"), "QWERTY"))
        }

        @Test
        fun baseOrgPathForTestOrganization() {
            val testOrgPath = "/ANNET/123456789"

            assertEquals(testOrgPath, createOrgPath(true, setOf("123456789"), "STAT"))
            assertEquals(testOrgPath, createOrgPath(true, setOf("123456789"), "FYLK"))
            assertEquals(testOrgPath, createOrgPath(true, setOf("123456789"), "KOMM"))
            assertEquals(testOrgPath, createOrgPath(true, setOf("123456789"), "IKS"))
            assertEquals(testOrgPath, createOrgPath(true, setOf("123456789"), "QWERTY"))
        }

        @Test
        fun orgIdsInCorrectOrder() {
            val expected = "/KOMMUNE/111222333/444555666/777888999"
            val result = createOrgPath(false, setOf("777888999", "444555666", "111222333"), "KOMM")
            assertEquals(expected, result)
        }

    }

    @Test
    fun orgPathCutCorrectForParents() {
        val completeOrgPath = "/STAT/123456789/987654321/987987987/654654654/321321321"
        assertEquals(completeOrgPath, cutOrgPathForParents(completeOrgPath, "321321321"))
        assertEquals("/STAT/123456789/987654321/987987987/654654654", cutOrgPathForParents(completeOrgPath, "654654654"))
        assertEquals("/STAT/123456789/987654321/987987987", cutOrgPathForParents(completeOrgPath, "987987987"))
        assertEquals("/STAT/123456789/987654321", cutOrgPathForParents(completeOrgPath, "987654321"))
        assertEquals("/STAT/123456789", cutOrgPathForParents(completeOrgPath, "123456789"))
    }

}
