package com.lucidity.haolu.duelking

import io.realm.RealmObject
import io.realm.annotations.Required

open class Card(var id: Int = 0, @Required var name: String = "") : RealmObject()