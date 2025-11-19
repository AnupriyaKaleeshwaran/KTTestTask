package com.example.testtask.data.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class User : RealmObject {
    @PrimaryKey
    var id: String = ""
    var email: String = ""
}