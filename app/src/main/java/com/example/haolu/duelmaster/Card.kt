package com.example.haolu.duelmaster

import io.realm.RealmObject
import io.realm.annotations.Required

open class Card(@Required var name: String = "") : RealmObject() {

}