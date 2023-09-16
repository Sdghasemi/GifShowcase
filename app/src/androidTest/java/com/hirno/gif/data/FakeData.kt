package com.hirno.gif.data

import com.hirno.gif.model.Gif
import com.hirno.gif.model.GifArrayResponse
import com.hirno.gif.model.SingleGifResponse

val testResponse get() = SingleGifResponse(
    data = Gif(
        id = "8DUxtTxFntY7lpJnzy",
        url = "https://gph.is/g/ZYvm2p4",
        _title = "GIF by Ryan Seslow",
        rating = "g",
        images = Gif.Images(
            original = Gif.Images.ImageInfo(
                width = 480,
                height = 417,
                url = "https://media3.giphy.com/media/YNzI0BYaAWMSIKu3Dg/giphy.gif?cid=6df7b0feaqw4bfw0s9j65ibft0xe8uavs268bnvnmqp8dv8l&ep=v1_gifs_random&rid=giphy.gif&ct=g",
            ),
            originalStill = Gif.Images.ImageInfo(
                width = 480,
                height = 417,
                url = "https://media3.giphy.com/media/YNzI0BYaAWMSIKu3Dg/giphy_s.gif?cid=6df7b0feaqw4bfw0s9j65ibft0xe8uavs268bnvnmqp8dv8l&ep=v1_gifs_random&rid=giphy_s.gif&ct=g",
            ),
        ),
    ),
)
val testArrayResponse get() = GifArrayResponse(
    data = arrayListOf(
        testResponse.data,
        Gif(
            id = "3ohze0PxVbRasIMicg",
            url = "http://gph.is/2rui4aG",
            _title = "major booth pinkpop 2017 GIF by Jillz",
            rating = "g",
            images = Gif.Images(
                original = Gif.Images.ImageInfo(
                    width = 480,
                    height = 320,
                    url = "https://media1.giphy.com/media/3ohze0PxVbRasIMicg/giphy.gif?cid=6df7b0fe6yzg3kt5gnjrj3h9bbdae393vpxxa26xyej4f8ni&ep=v1_gifs_random&rid=giphy.gif&ct=g",
                ),
                originalStill = Gif.Images.ImageInfo(
                    width = 480,
                    height = 320,
                    url = "https://media1.giphy.com/media/3ohze0PxVbRasIMicg/giphy_s.gif?cid=6df7b0fe6yzg3kt5gnjrj3h9bbdae393vpxxa26xyej4f8ni&ep=v1_gifs_random&rid=giphy_s.gif&ct=g",
                ),
            ),
        ),
        Gif(
            id = "l1E2kHo85GmrSJ6Za",
            url = "http://gph.is/2lU8hG8",
            _title = "GIF by A.M.T.G. G.G.",
            rating = "g",
            images = Gif.Images(
                original = Gif.Images.ImageInfo(
                    width = 480,
                    height = 364,
                    url = "https://media2.giphy.com/media/l1E2kHo85GmrSJ6Za/giphy.gif?cid=6df7b0fetnxwkn6gfmvm97lx0r3qg6aio1cis1m7a058o4jo&ep=v1_gifs_random&rid=giphy.gif&ct=g",
                ),
                originalStill = Gif.Images.ImageInfo(
                    width = 480,
                    height = 364,
                    url = "https://media2.giphy.com/media/l1E2kHo85GmrSJ6Za/giphy_s.gif?cid=6df7b0fetnxwkn6gfmvm97lx0r3qg6aio1cis1m7a058o4jo&ep=v1_gifs_random&rid=giphy_s.gif&ct=g",
                ),
            ),
        ),
    ),
)
val badRequest get() = Error(
    meta = Error.Meta(
        status = 400,
        message = "Bad request!",
    ),
)

val Error.message get() = meta.message