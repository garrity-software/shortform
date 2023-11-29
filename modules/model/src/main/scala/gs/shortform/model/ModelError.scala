package gs.shortform.model

import gs.shortform.error.ShortFormError

sealed trait ModelError extends ShortFormError

object ModelError:

  /**
   * Results from a [[Tag]] failing validation for being too short.
   *
   * @param candidate The candidate string.
   * @param minimumLength The minimum required length.
   */
  case class TagTooShort(candidate: String, minimumLength: Int) extends ModelError

  /**
   * Results from a [[Tag]] failing validation for being too long.
   *
   * @param candidate The candidate string.
   * @param maximumLength The maximum required length.
   */
  case class TagTooLong(candidate: String, maximumLength: Int) extends ModelError

end ModelError
