package com.utn.hwstore.entities

class Review(username: String, review: String, stars: Double, imgURL: String) {
    var username: String
    var review: String
    var stars: Double
    var image: String

    init {
        this.username = username
        this.review = review
        this.stars = stars
        this.image = imgURL
    }
}