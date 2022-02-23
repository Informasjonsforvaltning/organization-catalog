package no.digdir.organizationcatalog.utils

/**
 * Expect assertion style wrapper for jupiter assertions
 */

import org.junit.jupiter.api.Assertions

class Expect(_result: Any?){
    private val responseReader = TestResponseReader()

    val result = _result

    fun to_equal(expected: Any?) {
        Assertions.assertEquals(expected,result)
    }

    fun to_contain(expected: Any) {
        when(result) {
           is String -> Assertions.assertTrue(result.contains(expected as String))
           is List<*> -> Assertions.assertTrue(result.contains(expected))
           else -> throw AssertionError("Unexpected datatype in result");
        }
    }

    fun isomorphic_with_response_in_file(filename: String, resultLang: String) {
        val resultModel = responseReader.parseResponse(result as String, resultLang)
        val expectedModel = responseReader.getExpectedResponse(filename, "TURTLE")

        val isIsomorphic = expectedModel.isIsomorphicWith(resultModel)

        if(!isIsomorphic) println(result)

        Assertions.assertTrue(isIsomorphic)
    }
}
