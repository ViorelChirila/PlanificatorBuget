package com.example.planificatorbuget.model

import com.google.firebase.firestore.PropertyName

class TransactionCategoriesModel(
    @get:PropertyName("category_id")
    @set:PropertyName("category_id")
    var categoryId: String = "",
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = "",
    @get:PropertyName("category_name")
    @set:PropertyName("category_name")
    var categoryName: String = "",
    @get:PropertyName("category_icon")
    @set:PropertyName("category_icon")
    var categoryIcon: String = "",
) {
}