@file:JsModule("graphql")
@file:JsNonModule
package hu.sztaki.dsd.hasura.subset

// Simple mapping to some graphql functions, just enough so that it can be called from Kotlin

// export function buildSchema(
//  source: string | Source,
//  options?: BuildSchemaOptions & ParseOptions,
//): GraphQLSchema;
external fun buildSchema(source: String): dynamic

// export function graphqlSync(
//  schema: GraphQLSchema,
//  source: Source | string,
//  rootValue?: any,
//  contextValue?: any,
//  variableValues?: Maybe<{ [key: string]: any }>,
//  operationName?: Maybe<string>,
//  fieldResolver?: Maybe<GraphQLFieldResolver<any, any>>,
//  typeResolver?: Maybe<GraphQLTypeResolver<any, any>>,
//): ExecutionResult;
external fun graphqlSync(schema: dynamic, source: dynamic): dynamic

// export function getIntrospectionQuery(options?: IntrospectionOptions): string
external fun getIntrospectionQuery(): dynamic

external fun parse(query: String): dynamic
