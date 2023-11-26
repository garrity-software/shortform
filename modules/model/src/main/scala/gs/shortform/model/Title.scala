package gs.shortform.model

/**
  * The descriptive title for some resource.
  */
opaque type Title = String

object Title:

  def apply(value: String): Title = value

  given CanEqual[Title, Title] = CanEqual.derived

  extension (title: Title)
    def str(): String = title

end Title

