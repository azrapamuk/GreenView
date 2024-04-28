package com.example.spirala1

import android.os.Parcel
import android.os.Parcelable

enum class MedicinskaKorist(val opis: String) : Parcelable{
    SMIRENJE("Smirenje - za smirenje i relaksaciju"),
    PROTUUPALNO("Protuupalno - za smanjenje upale"),
    PROTIVBOLOVA("Protivbolova - za smanjenje bolova"),
    REGULACIJAPRITISKA("Regulacija pritiska - za regulaciju visokog/niskog pritiska"),
    REGULACIJAPROBAVE("Regulacija probave"),
    PODRSKAIMUNITETU("Podrška imunitetu");

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(opis)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MedicinskaKorist> {
        override fun createFromParcel(parcel: Parcel): MedicinskaKorist {
            val opis = parcel.readString()!!
            return entries.find { it.opis == opis }
                ?: throw IllegalArgumentException("Invalid MedicinskaKorist opis found in Parcel")
        }

        override fun newArray(size: Int): Array<MedicinskaKorist?> {
            return arrayOfNulls(size)
        }
    }

}
