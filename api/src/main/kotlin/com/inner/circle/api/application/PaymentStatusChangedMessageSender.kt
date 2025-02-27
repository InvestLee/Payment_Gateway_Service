package com.inner.circle.api.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.inner.circle.api.application.dto.PaymentStatusChangedResponse
import com.inner.circle.api.application.dto.PaymentStatusChangedSsePaymentRequest
import com.inner.circle.api.application.dto.PaymentStatusEventType
import com.inner.circle.core.service.dto.ConfirmPaymentCoreDto
import com.inner.circle.core.sse.SseConnectionPool
import com.inner.circle.exception.SseException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PaymentStatusChangedMessageSender(
    private val sseConnectionPool: SseConnectionPool,
    private val objectMapper: ObjectMapper
) {
    companion object {
        private val log = LoggerFactory.getLogger(PaymentStatusChangedMessageSender::class.java)
    }

    fun sendProcessChangedMessage(ssePaymentRequest: PaymentStatusChangedSsePaymentRequest) {
        val merchantId = ssePaymentRequest.merchantId
        val orderId = ssePaymentRequest.orderId
        val eventType = ssePaymentRequest.eventType

        try {
            val uniqueKey = merchantId.toString() + "_" + orderId
            val session =
                sseConnectionPool.getSessions(
                    uniqueKey
                )
            val eventData = PaymentStatusChangedResponse.of(eventType, orderId, merchantId)

            for (sseConnection in session) {
                sseConnection.sendMessage(eventType, eventData)
            }

            log.info(
                "sse message send. (merchantId: {}, orderId: {}, eventType: {})",
                merchantId,
                orderId,
                eventType
            )
        } catch (e: NoSuchElementException) {
            log.error("get sse session failed", e)
            throw SseException.ConnectionNotFoundException(merchantId.toString(), orderId)
        }
    }

    fun sendPaymentAuthResultMessage(
        statusEventType: PaymentStatusEventType,
        authResult: ConfirmPaymentCoreDto
    ) {
        val merchantId = authResult.merchantId
        val orderId = authResult.orderId
        val eventType = statusEventType.getEventType()

        try {
            val uniqueKey = merchantId.toString() + "_" + orderId
            val session =
                sseConnectionPool.getSessions(
                    uniqueKey
                )

            for (sseConnection in session) {
                sseConnection.sendMessage(eventType, authResult)
            }

            log.info(
                "sse message send. (merchantId: {}, orderId: {}, eventType: {})",
                merchantId,
                orderId,
                eventType
            )
        } catch (e: NoSuchElementException) {
            log.error("get sse session failed", e)
            throw SseException.ConnectionNotFoundException(merchantId.toString(), orderId)
        }
    }
}
