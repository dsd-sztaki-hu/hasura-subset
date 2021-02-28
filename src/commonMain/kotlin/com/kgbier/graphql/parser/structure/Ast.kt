package com.kgbier.graphql.parser.structure

data class Document(
        var definitions: List<Definition>
)

data class DefinitionExecutable(var definition: ExecutableDefinition) : Definition()
sealed class Definition

sealed class ExecutableDefinition
data class ExecutableDefinitionOperation(var definition: OperationDefinition) : ExecutableDefinition()
data class ExecutableDefinitionFragment(var definition: FragmentDefinition) : ExecutableDefinition()

data class OperationDefinitionOperation(var definition: Operation) : OperationDefinition()
data class OperationDefinitionSelectionSet(var selectionSet: List<Selection>) : OperationDefinition()
sealed class OperationDefinition {
    data class Operation(
            var operationType: OperationType,
            var name: String?,
            var variableDefinitions: List<VariableDefinition>,
            var directives: List<Directive>,
            var selectionSet: List<Selection>
    )
}

data class FragmentDefinition(
        var name: String,
        var typeCondition: TypeCondition,
        var directives: List<Directive>,
        var selectionSet: List<Selection>
)

data class InlineFragment(
        var typeCondition: TypeCondition?,
        var directives: List<Directive>,
        var selectionSet: List<Selection>
)

data class FragmentSpread(
        var name: String,
        var directives: List<Directive>
)

enum class OperationType {
    QUERY,
    MUTATION,
    SUBSCRIPTION
}

data class TypeCondition(var namedType: String)

data class Field(
        var alias: String?,
        var name: String,
        var arguments: List<Argument>,
        var directives: List<Directive>,
        var selectionSet: List<Selection>
)

data class SelectionField(var selection: Field) : Selection()
data class SelectionFragmentSpread(var selection: FragmentSpread) : Selection()
data class SelectionInlineFragment(var selection: InlineFragment) : Selection()
sealed class Selection

data class Directive(
        var name: String,
        var arguments: List<Argument>
)

data class Argument(
        var name: String,
        var value: Value
)

data class VariableDefinition(
        var variable: String,
        var type: String,
        var defaultValue: Value?
)

sealed class Value {
    data class ValueVariable(var name: String) : Value()
    data class ValueInt(var value: String) : Value()
    data class ValueFloat(var value: String) : Value()
    data class ValueString(var value: String) : Value()
    data class ValueBoolean(var value: Boolean) : Value()
    object ValueNull : Value()
    data class ValueEnum(var value: String) : Value()
    data class ValueList(var value: List<Value>) : Value()
    data class ValueObject(var value: List<ObjectField>) : Value()
}

data class ObjectField(var name: String, var value: Value)