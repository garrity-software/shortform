package gs.shortform.model

/**
  * Represents a _password reset token_ that a user can use to set a new 
  * password.
  *
  * @param token The unique token.
  * @param used Whether this token has been used.
  * @param createdAt The instant this token was created.
  * @param expiresAt The instant this token expires.
  */
case class PasswordReset(
  token: String,
  used: Boolean,
  createdAt: CreatedAt,
  expiresAt: ExpiresAt
)
