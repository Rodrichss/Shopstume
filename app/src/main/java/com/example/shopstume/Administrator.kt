package com.example.shopstume

class Administrator(idUser:Int, name:String, lastName:String, email:String, password:String)
    :User(idUser, name, lastName, email, password, "Administrador") {
}