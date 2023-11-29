package gs.shortform.crypto

import java.security.MessageDigest
import java.io.BufferedInputStream

object Sha256:
  /**
   * Calculate the SHA-256 hash for some data.
   *
   * @param data The data to hash.
   * @return The base64-encoded [[Hash]] value.
   */
  def calculateHash(data: Array[Byte]): Hash = 
    val digest: MessageDigest = MessageDigest.getInstance(Sha256.Algorithm)
    Hash.encode(digest.digest(data))

  /**
   * Consume the entire given stream and calculate its SHA-256 hash. This 
   * function _always_ closes the underlying stream.
   *
   * @param data The data to hash.
   * @return The base64-encoded [[Hash]] value.
   */
  def consumeToHash(data: BufferedInputStream): Hash =
    try
      // TODO: Need sum catz?
      val digest: MessageDigest = MessageDigest.getInstance(Sha256.Algorithm)
      val buffer: Array[Byte] = Array.ofDim[Byte](8192)
      var count: Int = data.read(buffer)
      while 
        count > 0
      do
        digest.update(buffer, 0, count)
        count = data.read(buffer)
      
      Hash.encode(digest.digest())
    finally
      data.close()

  /**
   * JCA Algorithm name for SHA-256.
   */
  val Algorithm: String = "SHA-256"

end Sha256
