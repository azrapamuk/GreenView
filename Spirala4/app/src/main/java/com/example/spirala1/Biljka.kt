package com.example.spirala1

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Biljka(
    @PrimaryKey(autoGenerate=true) var id:Long?=null,
    @ColumnInfo(name="naziv")val naziv: String,
    @ColumnInfo(name="family")var porodica: String,
    @ColumnInfo(name="medicinskoUpozorenje")var medicinskoUpozorenje: String,
    @ColumnInfo(name="medicinskeKoristi")val medicinskeKoristi: List<MedicinskaKorist>,
    @ColumnInfo(name="profilOkusa")val profilOkusa: ProfilOkusaBiljke?,
    @ColumnInfo(name="jela")var jela: List<String>,
    @ColumnInfo(name="klimatskiTipovi")val klimatskiTipovi: List<KlimatskiTip>,
    @ColumnInfo(name="zemljisniTipovi")var zemljisniTipovi: List<Zemljiste>,
    @ColumnInfo(name="onlineChecked")var onlineChecked: Boolean=false
): Parcelable
