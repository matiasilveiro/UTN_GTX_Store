package com.utn.hwstore.entities

class User(var uid: String, var enabled: Boolean, var name: String, var profileImage: String, var email: String) {

    constructor() : this("", false,"","","")
}