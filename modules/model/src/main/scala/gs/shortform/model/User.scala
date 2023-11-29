package gs.shortform.model

import gs.shortform.crypto.EncodedCredential

/**
  * Represents a ShortForm user. Users are uniquely identified by their 
  * _username_.
  *
  * @param username The user's unique identifier.
  * @param password The user's hashed, encoded password.
  * @param role This user's [[Role]].
  * @param status The current [[UserStatus]] of the user.
  * @param createdAt The instant this user account was created.
  */
case class User(
  username: Username,
  password: EncodedCredential,
  role: Role,
  status: UserStatus,
  createdAt: CreatedAt
)
