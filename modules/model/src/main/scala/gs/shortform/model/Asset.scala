package gs.shortform.model

import gs.uuid.UUID
import gs.shortform.crypto.Hash

case class Asset(
  externalId: UUID,
  title: Title,
  extension: Asset.Extension,
  hash: Hash,
  createdAt: CreatedAt,
  createdBy: Username
)

object Asset:

  /**
   * Represents a file extension for some [[Asset]].
   */
  opaque type Extension = String

  object Extension:

    def apply(value: String): Extension = value

    given CanEqual[Extension, Extension] = CanEqual.derived

    extension (ext: Extension)
      def str(): String = ext

  end Extension

end Asset
