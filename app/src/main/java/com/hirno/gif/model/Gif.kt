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
    var id: String = "",
    @Key("bitly_url")
    var url: String = "",
    @Key("title")
    var title: String = "",
    @Key("rating")
    var rating: String = "",
    @Key("images")
    var images: Images = Images(),
) : Parcelable {
    @Parcelize
    data class Images(
        @Key("original")
        var original: ImageInfo = ImageInfo(),
        @Key("original_still")
        var originalStill: ImageInfo = ImageInfo(),
    ) : Parcelable {
        @Parcelize
        data class ImageInfo(
            @Key("width")
            var width: Int = 0,
            @Key("height")
            var height: Int = 0,
            @Key("url")
            var url: String = "",
        ) : Parcelable {
            @IgnoredOnParcel
            val ratio get() = "$width:$height"
        }
    }
}