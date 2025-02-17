package com.inner.circle.infrabackoffice.adaptor.dto

import com.inner.circle.infrabackoffice.repository.entity.Currency
import com.inner.circle.infrabackoffice.repository.entity.PaymentType
import kotlinx.datetime.LocalDateTime

data class PaymentDto(
    val id: Long,
    val paymentKey: String,
    val cardNumber: String,
    val currency: Currency,
    val accountId: Long?,
    val merchantId: Long,
    val paymentType: PaymentType,
    val orderId: String,
    val orderName: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
