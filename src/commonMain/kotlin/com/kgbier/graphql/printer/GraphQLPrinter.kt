package com.kgbier.graphql.printer

import com.kgbier.graphql.parser.structure.*

/**
 * Generates GraphQL query from AST.
 *
 * Based on: https://github.com/graphql-java/graphql-java/issues/126
 * Author: Balazs. E. Pataki
 */
class GraphQLPrinter(
    val indentWidth: Int = 2
) {

    internal interface Consumer<T> {
        /**
         * Performs this operation on the given argument.
         *
         * @param t the input argument
         */
        fun accept(t: T)
    }

    fun print(document: Document): String {
        val printer = GraphQLPrinterImpl(document, indentWidth)
        return printer.print()
    }

    private class GraphQLPrinterImpl constructor(root: Document, indentWidth: Int) {
        private val builder = StringBuilder()
        private val root: Document
        private val indentWidth: Int
        private var indentLevel = 0

        init {
            this.root = root
            this.indentWidth = indentWidth
        }

        fun print(): String {
            print(root)
            return builder.toString()
        }

        private fun <T : Any> join(nodes: List<T>, delimeter: String) {
            val size = nodes.size
            for (i in 0 until size) {
                val node = nodes[i]
                print(node)
                if (i + 1 != size) {
                    builder.append(delimeter)
                }
            }
        }

        private fun <T> wrap(start: String, thing: T, end: String) {
            wrap(start, thing, end, object : Consumer<T> {
                override fun accept(t: T) {
                    builder.append(t)
                }
            })
        }

        private fun <T> wrap(start: String, thing: T, end: String, inner: Consumer<T>) {
            if (thing is List<*> && (thing as List<*>).isEmpty()) {
                return
            }
            builder.append(start)
            inner.accept(thing)
            builder.append(end)
        }

        private val newLine: String
            get() {
                val builder = StringBuilder()
                builder.append("\n")
                for (i in 0 until indentLevel) {
                    for (j in 0 until indentWidth) {
                        builder.append(" ")
                    }
                }
                return builder.toString()
            }

        private fun line(count: Int = 1) {
            for (i in 0 until count) {
                builder.append(newLine)
            }
        }

        private fun print(node: Any) {
            when(node) {
                is Document -> print(node)
                is DefinitionExecutable -> print(node)
                is ExecutableDefinitionOperation -> print(node)
                is ExecutableDefinitionFragment -> print(node)
                is OperationDefinition.Operation -> print(node)
                is OperationDefinitionOperation -> print(node.definition)
                is OperationDefinitionSelectionSet -> print(node.selectionSet)
                is FragmentDefinition -> print(node)
                is VariableDefinition -> print(node)
                is Value.ValueBoolean -> print(node)
                is Value.ValueEnum -> print(node)
                is Value.ValueFloat -> print(node)
                is Value.ValueInt -> print(node)
                is Value.ValueObject -> print(node)
                is Value.ValueString -> print(node)
                is Value.ValueVariable -> print(node)
                is Value.ValueList -> print(node)
                is Directive -> print(node)
                is Argument -> print(node)
                is ObjectField -> print(node)
                is Field -> print(node)
                is InlineFragment -> print(node)
                is FragmentSpread -> print(node)
                else -> throw RuntimeException("unknown type "+node)
            }
        }

        private fun print(node: Document) {
            for (defintition in node.definitions) {
                print(defintition)
                line()
            }
        }

        private fun print(node: DefinitionExecutable) {
            print(node.definition)
        }

        private fun print(node: ExecutableDefinitionOperation) {
            print(node.definition)
        }

        private fun print(node: ExecutableDefinitionFragment) {
            print(node.definition)
        }

        private fun print(node: OperationDefinition.Operation) {
            val name = node.name
            val operation = node.operationType
            val variableDefinitions = node.variableDefinitions
            val directives = node.directives
            val selectionSet = node.selectionSet
            if (name == null && variableDefinitions.isEmpty() && directives.isEmpty() && operation === OperationType.QUERY) {
                print(selectionSet)
            } else {
                if (operation === OperationType.QUERY) {
                    builder.append("query ")
                } else if (operation === OperationType.MUTATION) {
                    builder.append("mutation ")
                } else {
                    throw RuntimeException("unsupported operation")
                }
                if (name != null) {
                    builder.append(name)
                }
                wrap(
                    "(",
                    variableDefinitions,
                    ")",
                    object : Consumer<List<VariableDefinition>> {
                        override fun accept(t: List<VariableDefinition>) {
                            join(t, ", ")
                        }
                    })
                wrap(" ", directives, " ", object : Consumer<List<Directive>> {
                    override fun accept(t: List<Directive>) {
                        join(t, " ")
                    }
                })
                builder.append(" ")
                print(selectionSet)
            }
        }

        private fun print(node: List<Selection>) {
            if (node.isEmpty()) {
                return
            }
            builder.append(" {")
            indentLevel++
            line()
            node.forEachIndexed { index, selection ->
                when(selection) {
                    is SelectionField -> print(selection.selection)
                    is SelectionFragmentSpread -> print(selection.selection)
                    is SelectionInlineFragment -> print(selection.selection)
                }
                if (index == node.size-1) {
                    indentLevel--
                }
                line()
            }
            builder.append("}")

        }

        private fun print(node: VariableDefinition) {
            builder.append("\$")
            builder.append(node.variable)
            builder.append(": ")
            builder.append(node.type)
            val defaultValue = node.defaultValue
            if (defaultValue != null) {
                builder.append(" = ")
                print(defaultValue)
            }
        }

        private fun print(node: Directive) {
            builder.append("@")
            builder.append(node.name)
            wrap("(", node.arguments, ")", object : Consumer<List<Argument>> {
                override fun accept(t: List<Argument>) {
                    join(t, ", ")
                }
            })
        }

        private fun print(node: FragmentDefinition) {
            builder.append("fragment ")
            builder.append(node.name)
            builder.append(" on ")
            print(node.typeCondition)
            wrap(" ", node.directives, " ", object : Consumer<List<Directive>> {
                override fun accept(t: List<Directive>) {
                    join(t, " ")
                }
            })
            print(node.selectionSet)
        }

        private fun print(node: Value.ValueList) {
            wrap("[", node.value, "]", object : Consumer<List<Value>> {
                override fun accept(t: List<Value>) {
                    join(t, ", ")
                }
            })
        }

        private fun print(node: Value.ValueEnum) {
            builder.append(node.value)
        }

        private fun print(node: Value.ValueFloat) {
            builder.append(node.value)
        }

        private fun print(node: Value.ValueInt) {
            builder.append(node.value)
        }

        private fun print(node: Value.ValueObject) {
            wrap("{", node.value, "}", object : Consumer<List<ObjectField>> {
                override fun accept(t: List<ObjectField>) {
                    join(t, ", ")
                }
            })
        }

        private fun print(node: Value.ValueString) {
            wrap("\"", node.value, "\"")
        }

        private fun print(node: Value.ValueVariable) {
            builder.append("\$")
            builder.append(node.name)
        }

        private fun print(node: Argument) {
            builder.append(node.name)
            builder.append(": ")
            print(node.value)
        }

        private fun print(node: Field) {
            val alias = node.alias
            if (alias != null) {
                builder.append(alias)
                builder.append(": ")
            }
            builder.append(node.name)
            val arguments: List<Argument> = node.arguments
            wrap("(", arguments, ")", object : Consumer<List<Argument>> {
                override fun accept(t: List<Argument>) {
                    join(t, ", ")
                }
            })
            join(node.directives, " ")
            val selectionSet = node.selectionSet
            if (!selectionSet.isEmpty()) {
                print(selectionSet)
            }
        }

        private fun print(node: ObjectField) {
            builder.append(node.name)
            builder.append(": ")
            print(node.value)
        }

        private fun print(node: InlineFragment) {
            builder.append("... on ")
            builder.append(node.typeCondition?.namedType)
            builder.append(" ")
            join(node.directives, " ")
            print(node.selectionSet)
        }

        private fun print(node: FragmentSpread) {
            builder.append("...")
            builder.append(node.name)
            join(node.directives, " ")
        }
    }
}