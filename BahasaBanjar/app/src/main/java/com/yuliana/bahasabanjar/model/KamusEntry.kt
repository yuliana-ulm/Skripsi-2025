package com.yuliana.bahasabanjar.model

data class KamusEntry(
    var kata: String = "",
    var sukukata: String = "",
    var gambar: String = "",
    var abjad: String = "",
    var definisi_umum: List<Definisi> = emptyList(),
    var turunan: List<Turunan> = emptyList()
)

data class Definisi(
    var definisi: String = "",
    var kelaskata: String = "",
    var suara: String = "",
    var contoh: List<Contoh> = emptyList()
)

data class Contoh(
    var banjar: String = "",
    var indonesia: String = ""
)

data class Turunan(
    var kata: String = "",
    var sukukata: String = "",
    var gambar: String = "",
    var definisi_umum: List<Definisi> = emptyList()
)