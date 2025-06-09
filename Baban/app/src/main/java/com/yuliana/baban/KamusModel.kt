package com.yuliana.baban

data class Contoh(
    val banjar: String = "",
    val indonesia: String = ""
)

data class Definisi(
    val definisi: String = "",
    val kelaskata: String = "",
    val suara: String = "",
    val contoh: List<Contoh> = listOf()
)

data class Turunan(
    val kata: String = "",
    val sukukata: String = "",
    val gambar: String = "",
    val definisi_umum: List<Definisi> = listOf()
)

data class Kamus(
    val kata: String = "",
    val sukukata: String = "",
    val gambar: String = "",
    val definisi_umum: List<Definisi> = listOf(),
    val turunan: List<Turunan> = listOf()
)

