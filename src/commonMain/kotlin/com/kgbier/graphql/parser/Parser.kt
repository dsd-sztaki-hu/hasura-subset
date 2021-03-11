package com.kgbier.graphql.parser

import com.kgbier.graphql.parser.structure.*
import com.kgbier.graphql.parser.substring.Substring

internal interface Parser<A> {
    fun run(str: Substring): A?
}

internal fun <A> Parser<A>.parse(str: Substring) = run(str)

internal data class ParseResult<A>(val match: A?, val rest: Substring)

internal fun <A> Parser<A>.parse(str: String): ParseResult<A> {
    val substring = Substring(str)
    val match = parse(substring)
    return ParseResult(match, substring)
}

internal inline fun <A, B> Parser<A>.map(crossinline f: (A) -> B): Parser<B> = object : Parser<B> {
    override fun run(str: Substring): B? = this@map.parse(str)?.let(f)
}

internal fun <A> Parser<A>.erase(): Parser<Unit> = object : Parser<Unit> {
    override fun run(str: Substring) = this@erase.parse(str)?.let { Unit }
}

internal inline fun <A, B> Parser<A>.flatMap(crossinline f: (A) -> Parser<B>): Parser<B> = object : Parser<B> {
    override fun run(str: Substring): B? {
        val originalState = str.state
        val matchA = this@flatMap.parse(str)
        val parserB = matchA?.let(f)
        val matchB = parserB?.parse(str)
        return if (matchB == null) {
            str.state = originalState
            null
        } else matchB
    }
}

internal fun <A, B> zip(a: Parser<A>, b: Parser<B>, hint: String=""): Parser<Tuple2<A, B>> = object : Parser<Tuple2<A, B>> {
    override fun run(str: Substring): Tuple2<A, B>? {
        val originalState = str.state
        val resultA = a.parse(str) ?: return null
        val resultB = b.parse(str)
        return if (resultB == null) {
            str.state = originalState
            null
        } else Tuple2(resultA, resultB, hint)
    }
}

internal fun <A, B, C> zip(
        a: Parser<A>,
        b: Parser<B>,
        c: Parser<C>,
        hint: String=""
): Parser<Tuple3<A, B, C>> = zip(a, zip(b, c, hint), hint)
        .map { (a, bc) ->
            Tuple3(a, bc.first, bc.second, hint)
        }

internal fun <A, B, C, D> zip(
        a: Parser<A>,
        b: Parser<B>,
        c: Parser<C>,
        d: Parser<D>,
        hint: String=""
): Parser<Tuple4<A, B, C, D>> = zip(a, zip(b, c, d, hint), hint)
        .map { (a, bcd) ->
            Tuple4(a, bcd.first, bcd.second, bcd.third, hint)
        }

internal fun <A, B, C, D, E> zip(
        a: Parser<A>,
        b: Parser<B>,
        c: Parser<C>,
        d: Parser<D>,
        e: Parser<E>,
        hint: String=""
): Parser<Tuple5<A, B, C, D, E>> = zip(a, zip(b, c, d, e, hint), hint)
        .map { (a, bcde) ->
            Tuple5(a, bcde.first, bcde.second, bcde.third, bcde.fourth, hint)
        }

internal fun <A, B, C, D, E, F> zip(
        a: Parser<A>,
        b: Parser<B>,
        c: Parser<C>,
        d: Parser<D>,
        e: Parser<E>,
        f: Parser<F>,
        hint: String=""
): Parser<Tuple6<A, B, C, D, E, F>> = zip(a, zip(b, c, d, e, f, hint), hint)
        .map { (a, bcdef) ->
            Tuple6(a, bcdef.first, bcdef.second, bcdef.third, bcdef.fourth, bcdef.fifth, hint)
        }

internal fun <A, B, C, D, E, F, G> zip(
        a: Parser<A>,
        b: Parser<B>,
        c: Parser<C>,
        d: Parser<D>,
        e: Parser<E>,
        f: Parser<F>,
        g: Parser<G>,
        hint: String=""
): Parser<Tuple7<A, B, C, D, E, F, G>> = zip(a, zip(b, c, d, e, f, g, hint), hint)
        .map { (a, bcdefg) ->
            Tuple7(a, bcdefg.first, bcdefg.second, bcdefg.third, bcdefg.fourth, bcdefg.fifth, bcdefg.sixth, hint)
        }

internal fun <A, B, C, D, E, F, G, H> zip(
        a: Parser<A>,
        b: Parser<B>,
        c: Parser<C>,
        d: Parser<D>,
        e: Parser<E>,
        f: Parser<F>,
        g: Parser<G>,
        h: Parser<H>,
        hint: String=""
): Parser<Tuple8<A, B, C, D, E, F, G, H>> = zip(a, zip(b, c, d, e, f, g, h, hint), hint)
        .map { (a, bcdefgh) ->
            Tuple8(a, bcdefgh.first, bcdefgh.second, bcdefgh.third, bcdefgh.fourth, bcdefgh.fifth, bcdefgh.sixth, bcdefgh.seventh, hint)
        }

internal fun <A, B, C, D, E, F, G, H, I> zip(
        a: Parser<A>,
        b: Parser<B>,
        c: Parser<C>,
        d: Parser<D>,
        e: Parser<E>,
        f: Parser<F>,
        g: Parser<G>,
        h: Parser<H>,
        i: Parser<I>,
        hint: String=""
): Parser<Tuple9<A, B, C, D, E, F, G, H, I>> = zip(a, zip(b, c, d, e, f, g, h, i, hint), hint)
        .map { (a, bcdefghi) ->
            Tuple9(a, bcdefghi.first, bcdefghi.second, bcdefghi.third, bcdefghi.fourth, bcdefghi.fifth, bcdefghi.sixth, bcdefghi.seventh, bcdefghi.eighth, hint)
        }

internal fun <A, B, C, D, E, F, G, H, I, J> zip(
    a: Parser<A>,
    b: Parser<B>,
    c: Parser<C>,
    d: Parser<D>,
    e: Parser<E>,
    f: Parser<F>,
    g: Parser<G>,
    h: Parser<H>,
    i: Parser<I>,
    j: Parser<J>,
    hint: String=""
): Parser<Tuple10<A, B, C, D, E, F, G, H, I, J>> = zip(a, zip(b, c, d, e, f, g, h, i, j, hint), hint)
    .map { (a, bcdefghij) ->
        Tuple10(a, bcdefghij.first, bcdefghij.second, bcdefghij.third, bcdefghij.fourth, bcdefghij.fifth, bcdefghij.sixth, bcdefghij.seventh, bcdefghij.eighth, bcdefghij.ninth, hint)
    }
