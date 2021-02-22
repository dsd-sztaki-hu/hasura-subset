@file:JsModule("graphql-2-json-schema")
@file:JsNonModule
package hu.sztaki.dsd.graphql2JsonSchema

// Simple to fromIntrospectionQuery, just enough so that it can be called from Kotlin

external fun fromIntrospectionQuery(graphqlSchema: dynamic): dynamic

