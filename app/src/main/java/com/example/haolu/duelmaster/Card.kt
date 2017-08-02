package com.example.haolu.duelmaster

import io.realm.RealmObject
import io.realm.annotations.Required

open class Card(var id: Int = 0, @Required var name: String = "") : RealmObject()