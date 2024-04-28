package com.example.spirala1

import android.os.Parcel
import android.os.Parcelable

enum class ProfilOkusaBiljke(val opis: String): Parcelable {
        MENTA("Mentol - osvježavajući, hladan ukus"),
        CITRUSNI("Citrusni - osvježavajući, aromatičan"),
        SLATKI("Sladak okus"),
        BEZUKUSNO("Obični biljni okus - travnat, zemljast ukus"),
        LJUTO("Ljuto ili papreno"),
        KORIJENASTO("Korenast - drvenast i gorak ukus"),
        AROMATICNO("Začinski - topli i aromatičan ukus"),
        GORKO("Gorak okus");

        constructor(parcel: Parcel) : this(parcel.readString()!!) {
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeString(opis)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<ProfilOkusaBiljke> {
                override fun createFromParcel(parcel: Parcel): ProfilOkusaBiljke {
                        val opis = parcel.readString()!!
                        return ProfilOkusaBiljke.entries.find { it.opis == opis }!!
                }

                override fun newArray(size: Int): Array<ProfilOkusaBiljke?> {
                        return arrayOfNulls(size)
                }
        }

}