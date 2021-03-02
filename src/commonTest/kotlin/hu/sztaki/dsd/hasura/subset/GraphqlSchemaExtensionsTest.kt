package hu.sztaki.dsd.hasura.subset

//import com.soywiz.korio.async.suspendTest
//import com.soywiz.korio.file.std.uniVfs
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.*

class GraphqlSchemaExtensionsTest {

    lateinit var introspection: JsonObject

    init {
        val schema = tweetGraphql
        var graphqlIntro = graphqlSchemaToIntrospectedSchema(schema)
        introspection = Json.decodeFromString(graphqlIntro)

        // Doesn't want to work with JS target, doesn't find com.soywiz.korio references
        // Seemed to do everything as described here:
        // https://korlibs.soywiz.com/korio/
//        suspendTest {
//            val schemaFile = "./src/commonTest/resources/tweet.graphql".uniVfs
//            val schema = tweetGraphql
//            var graphqlIntro = graphqlSchemaToIntrospectedSchema(schema)
////        "./src/commonTest/resources/xxx.json".uniVfs.writeString(graphqlIntro)
//            introspection = Json.decodeFromString<JsonObject>(graphqlIntro)
//        }
    }

    @Test
    fun test_getType() {
        val type = introspection.getType("Tweet")
        assertNotNull(type)
        assertEquals(type.stringValue("name"), "Tweet")
    }

    @Test
    fun test_fieldNames() {
        val type = introspection.getType("Tweet")
        val fieldNames = type!!.fieldNames
        assertNotNull(fieldNames)
        assertEquals(arrayListOf("id", "body", "date", "Author", "Stats"), fieldNames)
    }

    @Test
    fun test_scalarFieldNames() {
        val type = introspection.getType("Tweet")
        val scalarNames = type!!.scalarFieldNames
        assertNotNull(scalarNames)
        assertEquals(arrayListOf("id", "body", "date"), scalarNames)
    }

    @Test
    fun test_getField() {
        val type = introspection.getType("Tweet")
        var field = type!!.getField("body")
        assertNotNull(field)
        assertEquals(field.stringValue("name"), "body")
        assertNull(type!!.getField("bodyx"))
    }

    @Test
    fun test_typeNameOfField() {
        val type = introspection.getType("Tweet")

        var fieldName = type!!.typeNameOfField("body")
        assertNotNull(fieldName)
        assertEquals(fieldName, "String")

        fieldName = type!!.typeNameOfField("Author")
        assertNotNull(fieldName)
        assertEquals(fieldName, "User")

        fieldName = type!!.typeNameOfField("id")
        assertNotNull(fieldName)
        assertEquals(fieldName, "ID")

        fieldName = type!!.typeNameOfField("bodyx")
        assertNull(fieldName)
    }

    @Test
    fun test_typeOfField() {
        val type = introspection.getType("Tweet")

        var fieldType = type!!.typeOfField("body")
        assertNotNull(fieldType)
        assertEquals(fieldType.stringValue("name"), "String")

        fieldType = type!!.typeOfField("Author")
        assertNotNull(fieldType)
        assertEquals(fieldType.stringValue("name"), "User")

        fieldType = type!!.typeOfField("id")
        assertNotNull(fieldType)
        assertEquals(fieldType.stringValue("name"), "ID")

        fieldType = type!!.typeOfField("bodyx")
        assertNull(fieldType)
    }
}
