package gs.shortform.model

/**
  * Unique name for a user.
  */
opaque type Username = String

object Username:

  def apply(value: String): Username = value

  given CanEqual[Username, Username] = CanEqual.derived

  extension (title: Username)
    def str(): String = title

end Username


