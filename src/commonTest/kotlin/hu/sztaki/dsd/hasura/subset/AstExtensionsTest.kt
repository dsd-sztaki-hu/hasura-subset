package hu.sztaki.dsd.hasura.subset

import com.kgbier.graphql.parser.GraphQLParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AstExtensionsTest {

    @Test
    fun test_findQueryOperation() {
        val ast = GraphQLParser.parseWithResult(graphqlQueryExample2)
        assertNotNull(ast.match)
        ast.match?.apply {
            assertNotNull(findQueryOperation("exampleQuery"))
            assertNull(findQueryOperation("exampleQueryXXX"))
        }
        println(ast)
    }

    @Test
    fun test_queryOperations() {
        val ast = GraphQLParser.parseWithResult(graphqlQueryExample2)
        assertNotNull(ast.match)
        ast.match?.apply {
            val ops = queryOperations
            assertEquals(1, ops.size)
        }
        println(ast)
    }

}