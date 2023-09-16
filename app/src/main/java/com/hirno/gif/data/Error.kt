package com.hirno.gif.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Model of server-returned errors
 */
@Parcelize
data class Error(
    @SerializedName("meta")
    val meta: Meta = Meta(),
) : Parcelable {
    @Parcelize
    data class Meta(
        @SerializedName("status")
        val status: Int = 0,
        @SerializedName("msg")
        val message: String = "",
        @SerializedName("response_id")
        val responseId: String = "",
    ) : Parcelable
}