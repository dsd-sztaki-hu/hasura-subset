package com.kgbier.graphql.parser.structure

data class Tuple2<out A, out B>(
        val first: A,
        val second: B,
        val hint: String?=""
)

data class Tuple3<out A, out B, out C>(
        val first: A,
        val second: B,
        val third: C,
        val hint: String?=""
)

data class Tuple4<out A, out B, out C, out D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val hint: String?=""
)

data class Tuple5<out A, out B, out C, out D, out E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E,
        val hint: String?=""
)

data class Tuple6<out A, out B, out C, out D, out E, out F>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E,
        val sixth: F,
        val hint: String?=""
)

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E,
        val sixth: F,
        val seventh: G,
        val hint: String?=""
)

data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E,
        val sixth: F,
        val seventh: G,
        val eighth: H,
        val hint: String?=""
)

data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E,
        val sixth: F,
        val seventh: G,
        val eighth: H,
        val ninth: I,
        val hint: String?=""
)
