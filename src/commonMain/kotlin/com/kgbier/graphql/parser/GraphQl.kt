package com.kgbier.graphql.parser

import com.kgbier.graphql.parser.Parsers.always
import com.kgbier.graphql.parser.Parsers.character
import com.kgbier.graphql.parser.Parsers.deferred
import com.kgbier.graphql.parser.Parsers.literal
import com.kgbier.graphql.parser.Parsers.maybe
import com.kgbier.graphql.parser.Parsers.never
import com.kgbier.graphql.parser.Parsers.notOneOf
import com.kgbier.graphql.parser.Parsers.oneOf
import com.kgbier.graphql.parser.Parsers.oneOrMore
import com.kgbier.graphql.parser.Parsers.zeroOrMore
import com.kgbier.graphql.parser.structure.*
import com.kgbier.kotlin.text.isDigit
import com.kgbier.kotlin.text.isLetterOrDigit

internal class GraphQl {

    /**
     * Language
     */

    // sourceChar -> '[\u0009\u000A\u000D\u0020-\uFFFF]'
    val sourceChar = character

    // name -> '[_A-Za-z][_0-9A-Za-z]'
    val name = Parsers.prefix { it.isLetterOrDigit() || it == '_' }
            .flatMap { if (it.isNotEmpty() && !it.first().isDigit()) always(it) else never() }

    // whiteSpace -> [ '\s' '\t' ]
    /**
     * White space is used to improve legibility of source text and act as separation between tokens,
     * and any amount of white space may appear before or after any token.
     * White space between tokens is not significant to the semantic meaning of a GraphQL Document,
     * however white space characters may appear within a String or Comment token.[1]
     *
     * [1] https://spec.graphql.org/June2018/#sec-White-Space
     */
    val whiteSpace = oneOf(listOf(
            literal(" "),
            literal("\t")
    ))

    // lineTerminator -> [ '\n' '\r' '\f' ]
    /**
     * Like white space, line terminators are used to improve the legibility of source text,
     * any amount may appear before or after any other token and have no significance to the semantic meaning of a GraphQL Document.
     * Line terminators are not found within any other token. [1]
     *
     * [1] https://spec.graphql.org/June2018/#sec-Line-Terminators
     */
    val lineTerminator = oneOf(listOf(
            literal("\n", true),
            literal("\r", true)
    ))

    // comma -> ','
    // Separate lexical tokens, can be trailing or used as line-terminators
    val comma = literal(",")

    // commentChar -> sourceChar != lineTerminator
    val commentChar = zip(
            notOneOf(listOf(lineTerminator)),
            character,
            "commentChar -> sourceChar != lineTerminator"
    ).map { (_, c) -> c }

    // comment -> " '#' { commentChar }? "
    /// Behaves like whitespace and may appear after any token, or before a line terminator
    val comment = zip(
            literal("#"),
            zeroOrMore(commentChar, "commentChar"),
        "comment -> \" '#' { commentChar }? \""
    ).erase()

    val tokenSeparator = zeroOrMore(oneOf(listOf(
            comment,
            lineTerminator,
            whiteSpace,
            comma),
        "tokenSeparator: comment, lineTerminator, whiteSpace, comma"
    )).erase()

    val requiredTokenSeparator = oneOrMore(oneOf(listOf(
            comment,
            lineTerminator,
            whiteSpace,
            comma),
        "requiredTokenSeparator: comment, lineTerminator, whiteSpace, comma"
    )).erase()

    /**
     * Values
     */

    // value -> [ variable intValue floatValue stringValue booleanValue nullValue listValue objectValue ]
    var value = deferred { valueDeferred }
    var valueDeferred: Parser<Value> = never()

    // negativeSign -> '-'
    val negativeSign = character('-')

    // digit -> [ '0' '1' '2' '3' '4' '5' '6' '7' '8' '9' ]
    val digit = oneOf(listOf(
            character('0'),
            character('1'),
            character('2'),
            character('3'),
            character('4'),
            character('5'),
            character('6'),
            character('7'),
            character('8'),
            character('9')
    ))

    // nonZeroDigit -> [ '1' '2' '3' '4' '5' '6' '7' '8' '9' ]
    val nonZeroDigit = oneOf(listOf(
            character('1'),
            character('2'),
            character('3'),
            character('4'),
            character('5'),
            character('6'),
            character('7'),
            character('8'),
            character('9')
    ))

    // integerPart -> [ " negativeSign? '0' "
    //                  " negativeSign? nonZeroDigit { digit? } " ]
    val integerPart = zip(
            maybe(negativeSign),
            oneOrMore(digit),
            "integerPart -> [ \" negativeSign? '0' \"\n" +
                    "    //                  \" negativeSign? nonZeroDigit { digit? } \" ]"
    ).flatMap { (negative, digits) ->
        if (digits.count() > 1 && digits.first() == '0') {
            never()
        } else {
            val maybeNegative = negative.wrappedValue ?: ""
            always("${maybeNegative}${digits.joinToString("")}")
        }
    }

    // intValue -> integerPart
    val intValue = integerPart.map { Value.ValueInt(it) }

    // exponentIndicator -> [ 'e' 'E' ]
    val exponentIndicator = oneOf(listOf(
            literal("e"),
            literal("E")
    ))

    // sign -> [ '+' '-' ]
    val sign = oneOf(listOf(
            character('+'),
            character('-')
    ))

    // fractionalPart -> " '.' { digit } "
    val fractionalPart = zip(
            literal("."),
            oneOrMore(digit),
            "fractionalPart -> \" '.' { digit } \""
    ).map { (_, digits) -> digits.joinToString("") }


    // exponentPart -> " exponentIndicator sign? { digit } "
    val exponentPart = zip(
            exponentIndicator,
            maybe(sign),
            oneOrMore(digit),
        "exponentPart -> \" exponentIndicator sign? { digit } \""
    ).map { (_, sign, digits) ->
        val maybeSign = sign.wrappedValue ?: "+"
        "e$maybeSign${digits.joinToString("")}"
    }

    // floatValue -> [ " integerPart fractionalPart "
    //                 " integerPart exponentPart "
    //                 " integerPart fractionalPart exponentPart " ]
    val floatValue = oneOf(listOf(
            zip(integerPart, fractionalPart, exponentPart, "floatValue -> \" integerPart fractionalPart exponentPart \"")
                    .map { (integerPart, fractionalPart, exponentPart) ->
                        Value.ValueFloat("$integerPart.$fractionalPart:$exponentPart")
                    },
            zip(integerPart, fractionalPart, "floatValue -> \" integerPart fractionalPart \"")
                    .map { (integerPart, fractionalPart) ->
                        Value.ValueFloat("$integerPart.$fractionalPart")
                    },
            zip(integerPart, exponentPart, "floatValue -> \" integerPart \"")
                    .map { (integerPart, exponentPart) ->
                        Value.ValueFloat("$integerPart:$exponentPart")
                    }),
            "floatValue -> [ \" integerPart fractionalPart \"\n" +
                    "    //                 \" integerPart exponentPart \"\n" +
                    "    //                 \" integerPart fractionalPart exponentPart \" ]"
    )

    // booleanValue -> [ 'true' 'false' ]
    val booleanValue = oneOf(listOf(
            literal("true").map { Value.ValueBoolean(true) },
            literal("false").map { Value.ValueBoolean(false) }
    ))

    // TODO: unicode literal handling
    // escapedUnicode -> [0-9A-Fa-f]{4}
    val escapedUnicode = zip(
            sourceChar,
            sourceChar,
            sourceChar,
            sourceChar,
            "escapedUnicode -> [0-9A-Fa-f]{4}"
    ).map { 'U' }

    // escapedCharacter -> [ '"' '\' '/' 'b' 'f' 'n' 'r' 't' ]
    val escapedCharacter = oneOf(listOf(
            character('\''),
            character('\\'),
            character('/'),
            character('b'),
            character('f'),
            character('n'),
            character('r'),
            character('t')
    ))

    // stringCharacter -> [ sourceCharacter != [ '"' '\' lineTerminator ]
    //                      " '\u' escapedUnicode "
    //                      " '\' escapedCharacter " ]
    val stringCharacter = oneOf(listOf(
            zip(notOneOf(listOf(literal("\""), literal("\\"), lineTerminator)),
                    sourceChar).map { (_, c) -> c },
            zip(literal("\\u"), escapedUnicode).map { (_, c) -> c },
            zip(literal("\\"), escapedCharacter).map { (_, c) -> c }),
            "stringCharacter -> [ sourceCharacter != [ '\"' '\\' lineTerminator ]\n" +
                    "    //                      \" '\\u' escapedUnicode \"\n" +
                    "    //                      \" '\\' escapedCharacter \" ]"
    )


    // stringValue -> [ " '"' { stringCharacter }? '"' "
    //                  " '"""' { blockStringCharacter }? '"""' " ]
    val stringValue = zip(
            literal("\""),
            zeroOrMore(stringCharacter, "stringCharacterg"),
            literal("\""),
        "stringValue -> [ \" '\"' { stringCharacter }? '\"' \"\n" +
                "    //                  \" '\"\"\"' { blockStringCharacter }? '\"\"\"' \" ]"
    ).map { (_, chars, _) -> Value.ValueString(chars.joinToString("")) }
    // TODO: block strings

    // nullValue -> 'null'
    val nullValue = literal("null").map { Value.ValueNull }

    // enumValue -> name != [ booleanValue nullValue ]
    val enumValue = zip(
            notOneOf(listOf(
                    booleanValue.erase(),
                    nullValue.erase()
            )),
            name,
            "enumValue -> name != [ booleanValue nullValue ]"
    ).map { (_, name) -> Value.ValueEnum(name) }

    // listValue -> [ " '[' ']' "
    //                " '[' { value } ']' " ]
    val listValue = oneOf(listOf(
            zip(literal("["),
                    tokenSeparator,
                    literal("]"),
                    "listValue -> [ \" '[' ']' \""
            ).map { Value.ValueList(emptyList()) },
            zip(literal("["),
                    tokenSeparator,
                    zeroOrMore(value, tokenSeparator, "value, tokenSeparator"),
                    tokenSeparator,
                    literal("]"),
                    "listValue -> \" '[' { value } ']' \""
            ).map { (_, _, values, _, _) -> Value.ValueList(values) }),
            "listValue -> [ \" '[' ']' \"\n" +
                    "    //                \" '[' { value } ']' \" ]"
    )

    // objectField -> " name ':' value "
    val objectField = zip(
            name,
            tokenSeparator,
            literal(":"),
            tokenSeparator,
            value,
            "objectField -> \" name ':' value \""
    ).map { (name, _, _, _, value) ->
        ObjectField(name, value)
    }

    // objectValue -> [ " '{' '}' "
    //                  " '{' { objectField } '}' " ]
    val objectValue = oneOf(listOf(
            zip(literal("{"),
                    tokenSeparator,
                    literal("}"),
                    "objectValue -> \" '{' '}' \""
            ).map { Value.ValueObject(emptyList()) },
            zip(literal("{"),
                    tokenSeparator,
                    oneOrMore(objectField, tokenSeparator),
                    tokenSeparator,
                    literal("}"),
                "objectValue -> \" '{' { objectField } '}' \""
            ).map { (_, _, fields, _, _) -> Value.ValueObject(fields) }),
            "objectValue -> [ \" '{' '}' \"\n" +
                    "    //                  \" '{' { objectField } '}' \" ]"
    )

    /**
     * Type
     */

    // type -> [ namedType listType nonNullType ]
    val type = deferred { typeDeferred }
    var typeDeferred: Parser<String> = never()

    // namedType -> name
    val namedType = name

    // listType -> " '[' type ']' "
    val listType = zip(
            literal("["),
            tokenSeparator,
            type,
            tokenSeparator,
            literal("]"),
            "listType -> \" '[' type ']' \""
    ).map { (_, _, type, _, _) -> "[$type]" }

    // nonNullType -> [ " namedType '!' "
    //                  " listType '!' " ]
    val nonNullType = oneOf(listOf(
            zip(listType, literal("!"), "nonNullType -> \" listType '!' \"").map { (type, _) -> "$type!" },
            zip(namedType, literal("!"), "snonNullType -> \" namedType '!' \"").map { (type, _) -> "$type!" }),
            "nonNullType -> [ \" namedType '!' \"\n" +
                "    //                  \" listType '!' \" ]"
    )

    /**
     * Variables
     */

    // defaultValue -> " '=' value "
    val defaultValue = zip(
            character('='),
            tokenSeparator,
            value,
            "defaultValue -> \" '=' value \""
    ).map { (_, _, value) -> value }

    // variable -> " '$' name "
    val variable = zip(
            literal("$"),
            name,
            "variable -> \" '\$' name \""
    ).map { (_, name) -> name }

    // Wrapper to use as a possible `value`
    val variableValue = variable.map { Value.ValueVariable(it) }

    // variableDefinition -> " variable ':' type defaultValue? "
    val variableDefinition = zip(
            variable,
            tokenSeparator,
            literal(":"),
            tokenSeparator,
            type,
            tokenSeparator,
            maybe(defaultValue),
            "variableDefinition -> \" variable ':' type defaultValue? \""
    ).map { (variable, _, _, _, type, _, defaultValue) ->
        VariableDefinition(variable, type, defaultValue.wrappedValue)
    }

    // variableDefinitions -> " '(' { variableDefinition } ')' "
    val variableDefinitions = zip(
            literal("("),
            tokenSeparator,
            zeroOrMore(variableDefinition, tokenSeparator, "variableDefinition, tokenSeparator"),
            tokenSeparator,
            literal(")"),
            "variableDefinitions -> \" '(' { variableDefinition } ')' \""
    ).map { (_, _, tokens, _, _) -> tokens }

    /**
     * Directives
     */

    // argument -> " name ':' value "
    val argument = zip(
            name,
            tokenSeparator,
            literal(":"),
            tokenSeparator,
            value,
            "argument -> \" name ':' value \""
    ).map { (name, _, _, _, value) ->
        Argument(name, value)
    }

    // arguments -> " '(' { argument } ')' "
    val arguments = zip(
            literal("("),
            tokenSeparator,
            zeroOrMore(argument, tokenSeparator, "argument, tokenSeparator"),
            tokenSeparator,
            literal(")"),
        "arguments -> \" '(' { argument } ')' \""
    ).map { (_, _, arguments, _, _) -> arguments }

    // directive -> " '@' name arguments? "
    val directive = zip(
            literal("@"),
            name,
            tokenSeparator,
            maybe(arguments),
            "directive -> \" '@' name arguments? \""
    ).map { (_, name, _, arguments) ->
        Directive(name, arguments.wrappedValue ?: emptyList())
    }

    // directives -> { directive }
    val directives = zeroOrMore(directive, tokenSeparator, "directive, tokenSeparator")

    /**
     * Selection sets
     */

    // selection -> [ field fragmentSpread inlineFragment ]
    val selection = deferred { selectionDeferred }
    var selectionDeferred: Parser<Selection> = never()

    // selectionSet -> " '{' { selection } '}' "
    val selectionSet = zip(
            literal("{"),
            tokenSeparator,
            zeroOrMore(selection, tokenSeparator, "selection, tokenSeparator"),
            tokenSeparator,
            literal("}"),
            "selectionSet -> \" '{' { selection } '}' \""
    ).map { (_, _, selections, _, _) -> selections }

    // alias -> " name ':' "
    val alias = zip(
            name,
            tokenSeparator,
            literal(":"),
            "alias -> \" name ':' \""
    ).map { (name, _, _) -> name }

    // field -> " alias? name arguments? directives? selectionSet? "
    val field = zip(
            maybe(alias),
            tokenSeparator,
            name,
            tokenSeparator,
            maybe(arguments),
            tokenSeparator,
            maybe(directives),
            tokenSeparator,
            maybe(selectionSet),
            "field -> \" alias? name arguments? directives? selectionSet? \""
    ).map { (alias, _, name, _, arguments, _, directives, _, selectionSet) ->
        Field(alias.wrappedValue,
                name,
                arguments.wrappedValue ?: emptyList(),
                directives.wrappedValue ?: emptyList(),
                selectionSet.wrappedValue ?: emptyList())
    }

    /**
     * Fragments
     */

    // fragmentName -> name != 'on'
    val fragmentName = zip(
            notOneOf(listOf(literal("on"))),
            name,
            "fragmentName -> name != 'on'"
    ).map { (_, name) -> name }

    // fragmentSpread -> " '...' fragmentName directives? "
    val fragmentSpread = zip(
            literal("..."),
            tokenSeparator,
            fragmentName,
            tokenSeparator,
            maybe(directives),
            "fragmentSpread -> \" '...' fragmentName directives? \""
    ).map { (_, _, fragmentName, _, directives) ->
        FragmentSpread(fragmentName, directives.wrappedValue ?: emptyList())
    }

    // typeCondition -> " 'on' namedType "
    val typeCondition = zip(
            literal("on"),
            requiredTokenSeparator,
            namedType,
        "typeCondition -> \" 'on' namedType \""
    ).map { (_, _, namedType) -> TypeCondition(namedType) }

    // fragmentDefinition -> " 'fragment' fragmentName typeCondition directives? selectionSet "
    val fragmentDefinition = zip(
            literal("fragment"),
            requiredTokenSeparator,
            fragmentName,
            requiredTokenSeparator,
            typeCondition,
            tokenSeparator,
            maybe(directives),
            tokenSeparator,
            selectionSet,
        "fragmentDefinition -> \" 'fragment' fragmentName typeCondition directives? selectionSet \""
    ).map { (_, _, fragmentName, _, typeCondition, _, directives, _, selectionSet) ->
        FragmentDefinition(fragmentName,
                typeCondition,
                directives.wrappedValue ?: emptyList(),
                selectionSet)
    }

    // inlineFragment -> " '...' typeCondition? directives? selectionSet "
    val inlineFragment = zip(
            literal("..."),
            tokenSeparator,
            maybe(typeCondition),
            tokenSeparator,
            maybe(directives),
            tokenSeparator,
            selectionSet,
            "inlineFragment -> \" '...' typeCondition? directives? selectionSet \""
    ).map { (_, _, typeCondition, _, directives, _, selectionSet) ->
        InlineFragment(typeCondition.wrappedValue, directives.wrappedValue ?: emptyList(), selectionSet)
    }

    /**
     * Document
     */

    // operationType -> [ 'query' 'mutation' 'subscription' ]
    val operationType = oneOf(listOf(
            literal("query").map { OperationType.QUERY },
            literal("mutation").map { OperationType.MUTATION },
            literal("subscription").map { OperationType.SUBSCRIPTION }
    ))

    // operationDefinition -> [ " operationType name? variableDefinitions? directives? selectionSet "
    //                          selectionSet ]
    val operationDefinition: Parser<OperationDefinition> = oneOf(listOf(
            zip(operationType,
                    tokenSeparator,
                    maybe(name),
                    tokenSeparator,
                    maybe(variableDefinitions),
                    tokenSeparator,
                    maybe(directives),
                    tokenSeparator,
                    maybe(selectionSet),
                "operationDefinition -> [ \" operationType name? variableDefinitions? directives? selectionSet selectionSet ]"
            ).map { (operationType, _, name, _, variableDefinitions, _, directives, _, selectionSet) ->
                OperationDefinitionOperation(OperationDefinition.Operation(
                        operationType,
                        name.wrappedValue,
                        variableDefinitions.wrappedValue ?: emptyList(),
                        directives.wrappedValue ?: emptyList(),
                        selectionSet.wrappedValue ?: emptyList()))
            },
            selectionSet.map { OperationDefinitionSelectionSet(it) }),
            "\"operationDefinition -> [ \\\" operationType name? variableDefinitions? directives? selectionSet selectionSet ]\""
    )

    // executableDefinition -> [ operationDefinition fragmentDefinition ]
    val executableDefinition = oneOf(listOf(
            operationDefinition.map { ExecutableDefinitionOperation(it) },
            fragmentDefinition.map { ExecutableDefinitionFragment(it) }),
            "executableDefinition -> [ operationDefinition fragmentDefinition ]"
    )

    // definition -> [ executableDefinition typeSystemDefinition TypeSystemExtension ]
    val definition = oneOf(listOf(
            executableDefinition.map { DefinitionExecutable(it) }
            // typeSystemDefinition, // GraphQL schema and other types not supported
            // TypeSystemExtension, // GraphQL schema and other types not supported
            ),
            "definition -> [ executableDefinition typeSystemDefinition TypeSystemExtension ]"
    )

    // document -> { definition }
    val document = oneOrMore(definition, tokenSeparator, "document -> { definition }")
            .map { Document(it) }

    init {
        valueDeferred = oneOf(listOf(
                variableValue,
                stringValue,
                objectValue,
                listValue,
                nullValue,
                booleanValue,
                enumValue,
                floatValue,
                intValue),
            "variableValue,\n" +
                    "                stringValue,\n" +
                    "                objectValue,\n" +
                    "                listValue,\n" +
                    "                nullValue,\n" +
                    "                booleanValue,\n" +
                    "                enumValue,\n" +
                    "                floatValue,\n" +
                    "                intValue"
        )

        typeDeferred = oneOf(listOf(
                nonNullType, // This must be first, otherwise won't work with [Int!]! types
                listType,
                namedType),
                "nonNullType, listType, namedType"
        )

        selectionDeferred = oneOf(listOf(
                field.map { SelectionField(it) },
                fragmentSpread.map { SelectionFragmentSpread(it) },
                inlineFragment.map { SelectionInlineFragment(it) }
        ))
    }
}