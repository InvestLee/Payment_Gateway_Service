package com.inner.circle.core.service

import com.inner.circle.core.domain.TransactionStatus
import com.inner.circle.core.service.dto.TransactionDto
import com.inner.circle.core.usecase.RefundPaymentUseCase
import com.inner.circle.exception.PaymentException
import com.inner.circle.infra.port.GetPaymentPort
import com.inner.circle.infra.port.GetTransactionPort
import com.inner.circle.infra.port.TransactionPort
import java.math.BigDecimal
import kotlinx.datetime.toJavaLocalDateTime
import org.springframework.stereotype.Service
import com.inner.circle.infra.repository.entity.TransactionStatus as InfraTransactionStatus

@Service
internal class RefundPaymentService(
    private val transactionPort: TransactionPort,
    private val getTransactionPort: GetTransactionPort,
    private val getPaymentPort: GetPaymentPort
) : RefundPaymentUseCase {
    override fun refundAll(
        accountId: Long,
        paymentKey: String
    ): TransactionDto {
        val payment =
            getPaymentPort.findByAccountIdAndPaymentKey(
                GetPaymentPort.FindByPaymentKeyRequest(
                    accountId = accountId,
                    paymentKey = paymentKey
                )
            )

        val infraTransactionDtoList =
            getTransactionPort.findAllByPaymentKey(
                GetTransactionPort.Request(
                    paymentKey = paymentKey
                )
            )

        val totalAmount =
            infraTransactionDtoList.fold(BigDecimal.ZERO) { acc, transaction ->
                acc + transaction.amount
            }

        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw PaymentException.AlreadyRefundException(paymentKey)
        }

        val infraUserCardDto =
            transactionPort.save(
                TransactionPort.Request(
                    id = null,
                    paymentKey = paymentKey,
                    amount = -totalAmount,
                    status = InfraTransactionStatus.REFUNDED,
                    reason = null,
                    requestedAt = infraTransactionDtoList[0].requestedAt.toJavaLocalDateTime()
                )
            )

        return TransactionDto(
            id = infraUserCardDto.id,
            paymentKey = infraUserCardDto.paymentKey,
            amount = infraUserCardDto.amount,
            status = TransactionStatus.of(infraUserCardDto.status),
            reason = infraUserCardDto.reason,
            requestedAt = infraUserCardDto.requestedAt,
            createdAt = infraUserCardDto.createdAt,
            updatedAt = infraUserCardDto.updatedAt
        )
    }

    override fun refundPartial(
        accountId: Long,
        paymentKey: String,
        amount: BigDecimal
    ): TransactionDto {
        val payment =
            getPaymentPort.findByAccountIdAndPaymentKey(
                GetPaymentPort.FindByPaymentKeyRequest(
                    accountId = accountId,
                    paymentKey = paymentKey
                )
            )

        val infraTransactionDtoList =
            getTransactionPort.findAllByPaymentKey(
                GetTransactionPort.Request(
                    paymentKey = paymentKey
                )
            )

        val totalAmount =
            infraTransactionDtoList.fold(BigDecimal.ZERO) { acc, transaction ->
                acc + transaction.amount
            }

        if (amount.compareTo(totalAmount) > 0) {
            throw PaymentException.ExceedRefundAmountException(
                paymentKey,
                totalAmount
            )
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw PaymentException.BadRefundAmountException(
                paymentKey,
                amount
            )
        }

        val infraUserCardDto =
            transactionPort.save(
                TransactionPort.Request(
                    id = null,
                    paymentKey = paymentKey,
                    amount = -amount,
                    status = InfraTransactionStatus.CANCELED,
                    reason = null,
                    requestedAt = infraTransactionDtoList[0].requestedAt.toJavaLocalDateTime()
                )
            )

        return TransactionDto(
            id = infraUserCardDto.id,
            paymentKey = infraUserCardDto.paymentKey,
            amount = infraUserCardDto.amount,
            status = TransactionStatus.of(infraUserCardDto.status),
            reason = infraUserCardDto.reason,
            requestedAt = infraUserCardDto.requestedAt,
            createdAt = infraUserCardDto.createdAt,
            updatedAt = infraUserCardDto.updatedAt
        )
    }
}
