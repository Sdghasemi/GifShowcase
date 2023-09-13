package com.hirno.gif.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName as Key

@Parcelize
data class SingleGifResponse(
    @Key("data")
    val data: Gif = Gif(),
) : Parcelable
@Parcelize
data class GifArrayResponse(
    @Key("data")
    val data: List<Gif> = listOf(),
) : Parcelable
@Parcelize
data class Gif(
    @Key("id")
    val id: String = "",
    @Key("bitly_url")
    val url: String = "",
    @Key("title")
    private val _title: String = "",
    @Key("rating")
    val rating: String = "",
    @Key("images")
    val images: Images = Images(),
) : Parcelable {
    val title get() = _title.takeUnless { it.isBlank() } ?: "No gif title"

    @Parcelize
    data class Images(
        @Key("original")
        val original: ImageInfo = ImageInfo(),
        @Key("original_still")
        val originalStill: ImageInfo = ImageInfo(),
    ) : Parcelable {
        @Parcelize
        data class ImageInfo(
            @Key("width")
            val width: Int = 0,
            @Key("height")
            val height: Int = 0,
            @Key("url")
            val url: String = "",
        ) : Parcelable {
            @IgnoredOnParcel
            val ratio get() = "$width:$height"
        }
    }
}