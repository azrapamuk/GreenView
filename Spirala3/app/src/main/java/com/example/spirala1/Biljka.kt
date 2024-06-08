package com.example.spirala1

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Biljka(
    val naziv: String,
    val porodica: String?,
    val medicinskoUpozorenje: String?,
    val medicinskeKoristi: List<MedicinskaKorist>?,
    val profilOkusa: ProfilOkusaBiljke,
    val jela: List<String>?,
    val klimatskiTipovi: List<KlimatskiTip>?,
    val zemljisniTipovi: List<Zemljiste>?,
): Parcelable