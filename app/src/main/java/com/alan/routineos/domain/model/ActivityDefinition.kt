package com.alan.routineos.domain.model

data class ActivityDefinition(
    val id: String,
    val title: String,
    val description: String,
    val systemId: String? = null,
    val isDeleted: Boolean = false
)
