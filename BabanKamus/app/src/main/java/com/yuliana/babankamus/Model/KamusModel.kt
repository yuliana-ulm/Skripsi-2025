package com.yuliana.babankamus.Model

data class Kamus(
    val kata: String = "",
    val sukukata: String = "",
    val definisi_umum: List<Definisi> = emptyList(),
    val turunan: List<Turunan> = emptyList()
)

data class Definisi(
    val definisi: String = "",
    val kelaskata: String = "",
    val contoh: List<Contoh> = emptyList()
)

data class Contoh(
    val banjar: String = "",
    val indonesia: String = ""
)

data class Turunan(
    val kata: String = "",
    val sukukata: String = "",
    val definisi_umum: List<Definisi> = emptyList()
)


