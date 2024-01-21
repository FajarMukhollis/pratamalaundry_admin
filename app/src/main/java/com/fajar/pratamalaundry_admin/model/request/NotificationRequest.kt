package com.fajar.pratamalaundry_admin.model.request

data class NotificationRequest(
	val notification: Notification,
	val to: String
)

data class Notification(
	val body: String,
	val title: String
)

