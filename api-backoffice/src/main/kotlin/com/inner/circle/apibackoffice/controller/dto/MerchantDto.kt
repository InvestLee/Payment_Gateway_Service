package com.inner.circle.apibackoffice.controller.dto

import com.inner.circle.corebackoffice.service.dto.MerchantDto as CoreMerchantDto

data class MerchantDto(
    val id: Long,
    val email: String,
    val token: String,
    val name: String
) {
    companion object {
        fun of(merchant: CoreMerchantDto): MerchantDto =
            MerchantDto(
                id = merchant.id,
                email = merchant.email,
                token = merchant.token,
                name = merchant.name
            )
    }
}
