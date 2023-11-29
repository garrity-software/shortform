package gs.shortform.crypto

/**
  * Represents an opaque encoded credential. This type does not track the type
  * of encoding used.
  */
opaque type EncodedCredential = String

object EncodedCredential:
  def apply(credential: String): EncodedCredential = credential

  extension (credential: EncodedCredential)
    def render(): String = credential
    def str(): String    = credential

end EncodedCredential
