package com.fajar.pratamalaundry_admin.model.request

import com.google.gson.annotations.SerializedName

data class DeleteRulesKomplainRequest(
    @SerializedName("id_rules_komplain")
    val id_rules_komplain: String
)
