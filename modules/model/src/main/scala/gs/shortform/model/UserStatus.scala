package gs.shortform.model

/**
  * Enumeration of statuses for user accounts:
  *
  *   - `active`: The account is active and working normally.
  *   - `locked`: The account cannot be used.
  *   - `initializing`: The account requires a password.
  *
  * @param name The unique name of the `UserStatus`.
  */
sealed abstract class UserStatus(val name: String)

object UserStatus:

  /**
   * Regular user accounts
   */
  case object Active extends UserStatus("active")

  /**
   * Locked user accounts -- the account cannot be used at all. Login will fail
   * and any existing sessions will reject the user.
   */
  case object Locked extends UserStatus("locked")

  /**
   * New accounts which require a password.
   */
  case object Initializing extends UserStatus("initializing")

  /**
   * List of all supported statuses.
   */
  val All: List[UserStatus] = List(Active, Locked, Initializing)

  given CanEqual[UserStatus, UserStatus] = CanEqual.derived

  /**
   * Given some string, select the appropriate [[UserStatus]].
   *
   * @param name The name to parse.
   * @return The status with the given name, or `None` if no such status exists.
   */
  def parse(name: String): Option[UserStatus] =
    All.find(_.name.equalsIgnoreCase(name))

end UserStatus

