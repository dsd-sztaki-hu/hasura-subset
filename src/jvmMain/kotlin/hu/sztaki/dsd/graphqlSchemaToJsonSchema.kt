package hu.sztaki.dsd

actual fun graphqlSchemaToJsonSchema(schema: String): String {
    println(""" parseGraphqlSchema in JVM called with '${schema}'""")
    return "TODO"
}