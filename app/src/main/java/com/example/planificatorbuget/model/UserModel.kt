package com.example.planificatorbuget.model

data class UserModel(
    val userId: String,
    val userName: String,
    val profession: String,
){
    fun toMap(): MutableMap<String,Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "user_name" to this.userName,
            "profession" to this.profession
        )
    }
}
