package com.example.spirala1

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "BiljkaBitmap",
    foreignKeys = [ForeignKey(
        entity = Biljka::class,
        parentColumns = ["id"],
        childColumns = ["idBiljke"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class BiljkaBitmap(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo var idBiljke: Long,
    @ColumnInfo(name = "bitmap") var bitmap: String

)

