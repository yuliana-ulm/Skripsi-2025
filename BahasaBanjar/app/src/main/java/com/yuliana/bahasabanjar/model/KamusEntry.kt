package com.yuliana.bahasabanjar.model

data class KamusEntry(
    val kata: String,
    val sukukata: String,
    val gambar: String,
    val abjad: String,
    val definisi_umum: List<Definisi>,
    val turunan: List<Turunan>
)

data class Definisi(
    val definisi: String,
    val kelaskata: String,
    val suara: String,
    val contoh: List<Contoh>
)

data class Turunan(
    val kata: String,
    val sukukata: String,
    val gambar: String,
    val definisi_umum: List<Definisi>
)

data class Contoh(
    val banjar: String,
    val indonesia: String
)
