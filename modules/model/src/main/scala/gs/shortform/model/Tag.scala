package gs.shortform.model

import gs.shortform.error.ShortFormError

/**
  * Arbitrary short string which can be used to annotate [[Post]]s.
  */
opaque type Tag = String

object Tag:

  object Constraints:

    /**
     * The minimum allowed length for tags.
     */
    val MinimumLength: Int = 1

    /**
     * The maximum allowed length for tags.
     */
    val MaximumLength: Int = 32

  end Constraints

  /**
   * Instantiate a new [[Tag]]. This function is unsafe.
   *
   * @param value The tag value.
   * @return The new tag.
   */
  def apply(value: String): Tag = value

  /**
   * Validate some candidate value to instantiate a new [[Tag]].
   *
   * @param value The candidate string value.
   * @return The new tag, or an error if the input was invalid.
   */
  def validate(value: String): Either[ShortFormError, Tag] = 
    if value.length() < Constraints.MinimumLength then
      Left(ModelError.TagTooShort(value, Constraints.MinimumLength))
    else if value.length() > Constraints.MaximumLength then
      Left(ModelError.TagTooLong(value, Constraints.MaximumLength))
    else
      Right(value)

  given CanEqual[Tag, Tag] = CanEqual.derived

  extension (title: Tag)
    /**
     * Render this [[Tag]] as a string.
     */
    def str(): String = title

end Tag

