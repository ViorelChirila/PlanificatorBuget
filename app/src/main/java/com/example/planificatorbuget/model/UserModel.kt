package com.example.planificatorbuget.model

import com.google.firebase.firestore.PropertyName

data class UserModel(
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = "",
    @get:PropertyName("user_name")
    @set:PropertyName("user_name")
    var userName: String = "",
    @get:PropertyName("initial_budget")
    @set:PropertyName("initial_budget")
    var initialBudget: Double = 0.0,
    @get:PropertyName("current_budget")
    @set:PropertyName("current_budget")
    var currentBudget: Double = 0.0,
    @get:PropertyName("avatar_url")
    @set:PropertyName("avatar_url")
    var avatarUrl: String = "",
){
    fun toMap(): MutableMap<String,Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "user_name" to this.userName,
            "initial_budget" to this.initialBudget,
            "current_budget" to this.currentBudget,
            "avatar_url" to this.avatarUrl
        )
    }
}
