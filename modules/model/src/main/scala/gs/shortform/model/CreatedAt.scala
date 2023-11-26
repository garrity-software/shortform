package gs.shortform.model

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
  * Represents the instant some resource was created.
  */
opaque type CreatedAt = Instant

object CreatedAt:

  def apply(value: Instant): CreatedAt = value

  def fromOffsetDateTime(value: OffsetDateTime): CreatedAt = value.toInstant()

  given CanEqual[CreatedAt, CreatedAt] = CanEqual.derived

  extension (createdAt: CreatedAt)
    def toInstant(): Instant = createdAt

    def toOffsetDateTime(): OffsetDateTime = 
      toInstant().atOffset(ZoneOffset.UTC)

end CreatedAt
