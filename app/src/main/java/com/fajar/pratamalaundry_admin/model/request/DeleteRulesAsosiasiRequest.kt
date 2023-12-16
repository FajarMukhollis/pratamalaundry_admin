package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class DeleteRulesAsosiasiRequest(
    @SerializedName("id_rules_asosiasi")
    val id_rules_asosiasi: String
)