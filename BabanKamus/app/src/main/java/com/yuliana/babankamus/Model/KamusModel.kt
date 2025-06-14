package com.yuliana.babankamus.Model

data class Kamus(
    val kata: String = "",
    val sukukata: String = "",
    val gambar: String = "",
    val definisi_umum: List<Definisi> = emptyList(),
    val turunan: List<Turunan> = emptyList()
)

data class Definisi(
    val definisi: String = "",
    val kelaskata: String = "",
    val suara: String = "",
    val contoh: List<Contoh> = emptyList()
)

data class Contoh(
    val banjar: String = "",
    val indonesia: String = ""
)

data class Turunan(
    val kata: String = "",
    val sukukata: String = "",
    val gambar: String = "",
    val definisi_umum: List<Definisi> = emptyList()
)

data class SuaraItem(
    val nama: String = "",
    val suara_base64: String = ""
)

data class GambarItem(
    val nama: String = "",
    val gambar_base64: String = ""
)



