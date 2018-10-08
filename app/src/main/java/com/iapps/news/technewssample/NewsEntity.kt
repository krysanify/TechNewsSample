package com.iapps.news.technewssample

import android.os.Parcel
import android.os.Parcelable
import java.util.Arrays

/**
 * This represents a news item
 */
data class NewsEntity(
    val title: String,
    val abstract: String,
    val url: String,
    val byline: String,
    val published_date: String,
    val multimedia: List<MediaEntity>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(MediaEntity)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(abstract)
        parcel.writeString(url)
        parcel.writeString(byline)
        parcel.writeString(published_date)
        parcel.writeTypedList(multimedia)
    }

    override fun describeContents(): Int {
        return Arrays.hashCode(
            arrayOf(
                title,
                abstract,
                url,
                byline,
                published_date
            )
        )
    }

    fun mediaUrl(index: Int = 0) = if (multimedia.isEmpty()) null else multimedia[index].url
    fun mediaCaption(fallback: String, index: Int = 0) =
        if (multimedia.isEmpty()) fallback else multimedia[index].caption

    companion object CREATOR : Parcelable.Creator<NewsEntity> {
        override fun createFromParcel(parcel: Parcel): NewsEntity {
            return NewsEntity(parcel)
        }

        override fun newArray(size: Int): Array<NewsEntity?> {
            return arrayOfNulls(size)
        }
    }
}

/**
 * This class represents a media item
 */
data class MediaEntity(
    val url: String,
    val format: String,
    val height: Int,
    val width: Int,
    val type: String,
    val subType: String,
    val caption: String,
    val copyright: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(format)
        parcel.writeInt(height)
        parcel.writeInt(width)
        parcel.writeString(type)
        parcel.writeString(subType)
        parcel.writeString(caption)
        parcel.writeString(copyright)
    }

    override fun describeContents(): Int {
        return Arrays.hashCode(
            arrayOf(
                url,
                format,
                height,
                width,
                type,
                subType,
                caption,
                copyright
            )
        )
    }

    companion object CREATOR : Parcelable.Creator<MediaEntity> {
        override fun createFromParcel(parcel: Parcel): MediaEntity {
            return MediaEntity(parcel)
        }

        override fun newArray(size: Int): Array<MediaEntity?> {
            return arrayOfNulls(size)
        }
    }
}
