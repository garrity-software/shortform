package gs.shortform.model

/**
  * Enumeration of roles for users.
  *
  *   - `poster`: Allowed to upload files and post content.
  *   - `commenter`: Allowed to post comments and engage in discussion.
  *
  * @param name The unique name of the Role.
  */
sealed abstract class Role(val name: String)

object Role:

  /**
   * Role for users allowed to upload files and post content.
   */
  case object Poster extends Role("poster")

  /**
   * Role for users allowed to post comments.
   */
  case object Commenter extends Role("commenter")

  /**
   * List of all supported roles.
   */
  val All: List[Role] = List(Poster, Commenter)

  given CanEqual[Role, Role] = CanEqual.derived

  /**
   * Given some string, select the appropriate [[Role]].
   *
   * @param name The name to parse.
   * @return The role with the given name, or `None` if no such role exists.
   */
  def parse(name: String): Option[Role] =
    All.find(_.name.equalsIgnoreCase(name))

end Role
