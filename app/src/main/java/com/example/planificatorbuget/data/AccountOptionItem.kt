package com.example.planificatorbuget.data

import androidx.compose.ui.graphics.painter.Painter

data class AccountOptionItem(val name: String, val icon: Painter, val onClick: () -> Unit)
