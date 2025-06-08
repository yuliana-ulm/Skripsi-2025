package com.yuliana.bahasabanjar.admin

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.yuliana.bahasabanjar.R
import com.yuliana.bahasabanjar.admin.SimpanKamusLocal

class tambah_turunan : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_turunan)

        fun ambilDataTurunan(listViewTurunan: List<View>): List<SimpanKamusLocal.Turunan> {
            val daftarTurunan = mutableListOf<SimpanKamusLocal.Turunan>()

            for (view in listViewTurunan) {
                val kata = view.findViewById<EditText>(R.id.etTurunanKata).text.toString()
                val sukukata = view.findViewById<EditText>(R.id.etTurunanSuku).text.toString()
                val gambar = view.findViewById<EditText>(R.id.etTurunanGambar).text.toString()
                val definisi = view.findViewById<EditText>(R.id.etTurunanDefinisi).text.toString()
                val kelaskata = view.findViewById<EditText>(R.id.etTurunanKelas).text.toString()
                val contohBanjar =
                    view.findViewById<EditText>(R.id.etTurunanContohBanjar).text.toString()
                val contohIndo =
                    view.findViewById<EditText>(R.id.etTurunanContohIndo).text.toString()
                val suara = view.findViewById<EditText>(R.id.etTurunanSuara).text.toString()

                val turunan = SimpanKamusLocal.Turunan(
                    kata = kata,
                    sukukata = sukukata,
                    gambar = gambar,
                    definisi_umum = listOf(
                        SimpanKamusLocal.Definisi(
                            definisi = definisi,
                            kelaskata = kelaskata,
                            contoh = listOf(SimpanKamusLocal.Contoh(contohBanjar, contohIndo)),
                            suara = suara
                        )
                    )
                )
                daftarTurunan.add(turunan)
            }

            return daftarTurunan
        }
    }
}
