package com.kgbier.graphql.printer

import com.kgbier.graphql.parser.structure.*

/**
 * Generates GraphQL query from AST.
 *
 * Based on: https://github.com/graphql-java/graphql-java/issues/126
 * Author: Balazs. E. Patakis
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

    fun print(document: Document): String? {
        val printer = GraphQLPrinterImpl(document, indentWidth)
        return printer.print()
    }

    private class GraphQLPrinterImpl constructor(root: Document, indentWidth: Int) {
        private val builder = StringBuilder()
        private val root: Document
        private val indentWidth: Int
        private var indentLevel = 0
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
            if (thing is List<*> && (thing as List<T>).isEmpty()) {
                return
            }
            builder.append(start)
            inner.accept(thing)
            builder.append(end)
        }

        private val newLine: String
            private get() {
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
                is Document -> print(node as Document)
                is DefinitionExecutable -> print(node as DefinitionExecutable)
                is ExecutableDefinitionOperation -> print(node as ExecutableDefinitionOperation)
                is ExecutableDefinitionFragment -> print(node as ExecutableDefinitionFragment)
                is OperationDefinition.Operation -> print(node as OperationDefinition.Operation)
                is OperationDefinitionOperation -> print((node as OperationDefinitionOperation).definition)
                is OperationDefinitionSelectionSet -> print((node as OperationDefinitionSelectionSet).selectionSet)
                is FragmentDefinition -> print(node as FragmentDefinition)
                is VariableDefinition -> print(node as VariableDefinition)
                is Value.ValueList -> print(node as Value.ValueList)
                is Value.ValueBoolean -> print(node as Value.ValueBoolean)
                is Value.ValueEnum -> print(node as Value.ValueEnum)
                is Value.ValueFloat -> print(node as Value.ValueFloat)
                is Value.ValueInt -> print(node as Value.ValueInt)
                is Value.ValueObject -> print(node as Value.ValueObject)
                is Value.ValueString -> print(node as Value.ValueString)
                is Value.ValueVariable -> print(node as Value.ValueVariable)
                is Value.ValueList -> print(node as Value.ValueList)
                is Directive -> print(node as Directive)
                is Argument -> print(node as Argument)
                is ObjectField -> print(node as ObjectField)
                is Field -> print(node as Field)
                is InlineFragment -> print(node as InlineFragment)
                is FragmentSpread -> print(node as FragmentSpread)
                else -> throw RuntimeException("unknown type "+node)
            }
        }

        private fun print(node: Document) {
            for (defintition in node.definitions) {
                print(defintition)
                line(2)
            }
            line()
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
                wrap<List<VariableDefinition>>(
                    "(",
                    variableDefinitions,
                    ")",
                    object : Consumer<List<VariableDefinition>> {
                        override fun accept(definitions: List<VariableDefinition>) {
                            join(definitions, ", ")
                        }
                    })
                wrap<List<Directive>>(" ", directives, " ", object : Consumer<List<Directive>> {
                    override fun accept(dirs: List<Directive>) {
                        join(dirs, " ")
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
                override fun accept(arguments: List<Argument>) {
                    join(arguments, ", ")
                }
            })
        }

        private fun print(node: FragmentDefinition) {
            builder.append("fragment ")
            builder.append(node.name)
            builder.append(" on ")
            print(node.typeCondition)
            wrap(" ", node.directives, " ", object : Consumer<List<Directive>> {
                override fun accept(directives: List<Directive>) {
                    join(directives, " ")
                }
            })
            print(node.selectionSet)
        }

        private fun print(node: Value.ValueList) {
            wrap("[", node.value, "]", object : Consumer<List<Value>> {
                override fun accept(values: List<Value>) {
                    join(values, ", ")
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
                override fun accept(fields: List<ObjectField>) {
                    join(fields, ", ")
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
            wrap<List<Argument>>("(", arguments, ")", object : Consumer<List<Argument>> {
                override fun accept(args: List<Argument>) {
                    join(args, ", ")
                }
            })
            join(node.directives, " ")
            val selectionSet = node.selectionSet
            if (selectionSet != null) {
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

        init {
            this.root = root
            this.indentWidth = indentWidth
        }
    }
}