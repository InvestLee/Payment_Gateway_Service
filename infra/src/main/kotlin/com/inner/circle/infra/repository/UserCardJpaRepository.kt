package com.inner.circle.infra.repository

import com.inner.circle.infra.repository.entity.UserCardEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserCardJpaRepository : JpaRepository<UserCardEntity, String> {
    fun findByAccountIdAndIsRepresentative(
        accountId: Long,
        isRepresentative: Boolean
    ): UserCardEntity?

    fun findByAccountIdOrderByIdAsc(accountId: Long): List<UserCardEntity>

    fun findAllByOrderByAccountIdAscIdAsc(): List<UserCardEntity>

    fun findById(id: Long): UserCardEntity

    fun deleteById(id: Long)
}
