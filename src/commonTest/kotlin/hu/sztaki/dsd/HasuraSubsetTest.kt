package hu.sztaki.dsd

import kotlin.test.Test
import kotlin.test.assertEquals

class HasuraSubsetTest {

    @Test
    fun testIfJsonToUpsertWorks() {
        val hasuraSubset = HasuraSubset()
        for (test in tests) {
            println("Test: "+test.description)
            val result = hasuraSubset.jsonToUpsert(test.jsonResult)
            assertEquals(test.expected, result)
        }
    }
}