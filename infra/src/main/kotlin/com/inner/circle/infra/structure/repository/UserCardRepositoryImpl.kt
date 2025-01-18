package com.inner.circle.infra.structure.repository

import com.inner.circle.infra.structure.repository.entity.UserCardEntity
import org.springframework.stereotype.Repository

@Repository
internal class UserCardRepositoryImpl(
    private val userCardJpaRepository: UserCardJpaRepository
) : UserCardRepository {
    override fun findByUserId(userId: Long?): UserCardEntity? =
        userCardJpaRepository.findByUserId(userId)
}
