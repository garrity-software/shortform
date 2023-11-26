package gs.shortform.model

/**
  * Username of the user who created some resource.
  */
opaque type CreatedBy = String

object CreatedBy:

  // TODO: Create from username.
  def apply(value: String): CreatedBy = value

  given CanEqual[CreatedBy, CreatedBy] = CanEqual.derived

  extension (createdBy: CreatedBy)
    def str(): String = createdBy

end CreatedBy
