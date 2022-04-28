package com.example.kotlinchatapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class User (
    val uid : String,
    val profileUrl : String,
    val username : String
) : Parcelable {
    constructor():this("","","")
}