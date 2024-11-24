package com.example.mediaplayer

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track")
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist: String,
    val path: String,
    val coverPath: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        title = parcel.readString().orEmpty(),
        artist = parcel.readString().orEmpty(),
        path = parcel.readString().orEmpty(),
        coverPath = parcel.readString().orEmpty()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(path)
        parcel.writeString(coverPath)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track = Track(parcel)
        override fun newArray(size: Int): Array<Track?> = arrayOfNulls(size)
    }
}
