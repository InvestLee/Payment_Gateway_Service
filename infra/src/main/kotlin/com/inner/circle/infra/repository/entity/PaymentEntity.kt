package com.inner.circle.infra.repository.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "payment")
data class PaymentEntity(
    @Id
    @Column(name = "payment_key", nullable = false)
    val paymentKey: String,
    @Column(nullable = false)
    val currency: String,
    @Column(name = "user_id")
    val userId: Long?,
    @Column(name = "merchant_id", nullable = false)
    val merchantId: String,
    @Column(name = "payment_type", nullable = false)
    val paymentType: String,
    @Column(name = "order_id", nullable = false)
    val orderId: String,
    @Column(name = "order_name")
    val orderName: String?
) : BaseEntity()
