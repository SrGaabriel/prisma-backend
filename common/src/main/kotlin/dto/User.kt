package io.github.prismaplatform.common.dto

data class UserDto(
    val name: String
)

data class UserDtoResponse(
    val user: UserDto
)

data class UserSignupRequest(
    val name: String,
    val email: String,
    val password: String
)

