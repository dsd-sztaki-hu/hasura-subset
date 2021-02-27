package com.kgbier.graphql.parser.substring

data class SubstringState(
 val range: IntRange,
 val row: Int,
 val col: Int,
)

class Substring(string: String) : CharSequence {
    private val backingString = string
    private var range: IntRange = 0..string.length
    private var row: Int = 1
    private var col: Int = 1

    var state: SubstringState
        get() = SubstringState(range, row, col)
        set(value) {
            range = value.range
            row = value.row
            col = value.col
        }

    fun newLine() {
        row += 1
        col = 0 // advance will step to 1
    }

    fun advance(characters: Int = 1) {
        range = (range.first + characters)..range.last
        col += characters
    }

    override val length: Int
        get() = range.last - range.first

    override fun get(index: Int): Char = backingString[index + range.first]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
            backingString.subSequence(startIndex + range.first, endIndex + range.first)


    override fun toString(): String = backingString.substring(range.first, range.last)
}
