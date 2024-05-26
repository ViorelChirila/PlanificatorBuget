package com.example.planificatorbuget.model

data class UserModel(
    val userId: String,
    val userName: String,
    val profession: String,
    val initialBudget: Double = 0.0,
    val avatarUrl: String,
){
    fun toMap(): MutableMap<String,Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "user_name" to this.userName,
            "profession" to this.profession,
            "initial_budget" to this.initialBudget,
            "avatar_url" to this.avatarUrl
        )
    }
}
