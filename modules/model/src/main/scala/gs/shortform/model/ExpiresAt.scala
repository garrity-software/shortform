package gs.shortform.model

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
  * Represents the instant some resource expires.
  */
opaque type ExpiresAt = Instant

object ExpiresAt:

  def apply(value: Instant): ExpiresAt = value

  def fromOffsetDateTime(value: OffsetDateTime): ExpiresAt = value.toInstant()

  given CanEqual[ExpiresAt, ExpiresAt] = CanEqual.derived

  extension (expiresAt: ExpiresAt)
    /**
     * Convert this value to an `Instant`.
     */
    def toInstant(): Instant = expiresAt

    /**
     * Convert this value to an `OffsetDateTime` with the UTC offset.
     */
    def toOffsetDateTime(): OffsetDateTime = 
      toInstant().atOffset(ZoneOffset.UTC)

end ExpiresAt

