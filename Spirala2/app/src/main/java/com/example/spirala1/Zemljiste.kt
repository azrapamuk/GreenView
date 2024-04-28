package com.example.spirala1

import android.os.Parcel
import android.os.Parcelable

enum class Zemljiste(val naziv: String) : Parcelable{
    PJESKOVITO("Pjeskovito zemljište"),
    GLINENO("Glinеno zemljište"),
    ILOVACA("Ilovača"),
    CRNICA("Crnica"),
    SLJUNOVITO("Šljunovito zemljište"),
    KRECNJACKO("Krečnjačko zemljište");

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(naziv)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Zemljiste> {
        override fun createFromParcel(parcel: Parcel): Zemljiste {
            val naziv = parcel.readString()!!
            return Zemljiste.entries.find { it.naziv == naziv }!!
        }

        override fun newArray(size: Int): Array<Zemljiste?> {
            return arrayOfNulls(size)
        }
    }
}
