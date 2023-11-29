package gs.shortform.crypto.argon2

import org.bouncycastle.crypto.params.Argon2Parameters

/** Represents the output of [[Argon2]] being applied to some input.
  *
  * @param hash
  *   The hashed representation of the data.
  * @param parameters
  *   The Argon2 parameters used to produce the hash.
  */
case class Argon2Output(
  hash: Array[Byte],
  parameters: Argon2Parameters
)
