package com.example.spirala1


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Biljka(
    val naziv: String?,
    val porodica: String?,
    val medicinskoUpozorenje: String?,
    val medicinskeKoristi: List<MedicinskaKorist>?,
    val profilOkusa: ProfilOkusaBiljke,
    val jela: List<String>?,
    val klimatskiTipovi: List<KlimatskiTip>?,
    val zemljisniTipovi: List<Zemljiste>?
): Parcelable