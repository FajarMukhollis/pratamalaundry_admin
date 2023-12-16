package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class EditRulesAsosiasiRequest(
    @SerializedName("id_rules_asosiasi")
    val id_rules_asosiasi: String,

    @SerializedName("aturan")
    val aturan: String
)