package gs.shortform.crypto

import java.util.Base64

/** Represents any hashed value. All hashed values are Base64 encoded.
  *
  * @param value
  *   The Base64 encoded value of the hash.
  */
opaque type Hash = String

object Hash:

  /** Base64 encode some raw hashed data and store it as a `Hash`.
    *
    * @param raw
    *   The raw data.
    * @return
    */
  def encode(raw: Array[Byte]): Hash =
    Base64.getEncoder().encodeToString(raw)

  /** Base64 decode some encoded, hashed data and return the raw hashed bytes.
    *
    * @param hash
    *   The hash to decode.
    * @return
    *   The raw hashed bytes.
    */
  def decode(hash: Hash): Array[Byte] =
    Base64.getDecoder().decode(hash)

  given CanEqual[Hash, Hash] = CanEqual.derived

  extension (hash: Hash) def str: String = hash

end Hash
