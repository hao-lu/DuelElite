package com.lucidity.haolu.duelking.model

import io.realm.RealmObject
import io.realm.annotations.Required

/**
 * The RealmObject type used in the tcg.realm file
 *
 * @param name Is required
 */
open class OldCard(var id: Int = 0, @Required var name: String = "") : RealmObject()