package hu.sztaki.dsd.hasura.subset

import com.kgbier.graphql.parser.structure.*

/**
 * Finds an operation by name.
 */
fun Document.findQueryOperation(name: String) = queryOperations.find { it.name == name }

/**
 * List QUERY type operations
 */
val Document.queryOperations: List<OperationDefinition.Operation>
    get() {
        return definitions
            .filter {
                when(it) {
                    is DefinitionExecutable ->
                        when(it.definition) {
                            is ExecutableDefinitionOperation -> true
                            is ExecutableDefinitionFragment -> false
                        }
                }
            }.map {
                it.operation
            }
    }

/**
 * Get the operation from an ExecutableDefinitionOperation when you definitely know that this Definition
 * is an ExecutableDefinitionOperation
 */
val Definition.operation: OperationDefinition.Operation
        get() = (((this as DefinitionExecutable)
            .definition as ExecutableDefinitionOperation)
            .definition as OperationDefinitionOperation)
            .definition
