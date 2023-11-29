package gs.shortform.model

import gs.uuid.UUID

/**
  * Represents a comment. All comments are associated to some [[Post]], but
  * may also be replies to other comments.
  *
  * Comments cannot currently be edited.
  *
  * @param externalId Globally unique identifier for this comment.
  * @param createdAt Instant this comment was created.
  * @param createdBy User who created this comment.
  * @param rendered The pre-rendered text contents of this comment.
  * @param depth The depth of this comment.
  * @param parent The parent comment of this comment.
  */
case class Comment(
  externalId: UUID,
  createdAt: CreatedAt,
  createdBy: Username,
  rendered: String,
  depth: Comment.Depth,
  parent: Option[UUID]
)

object Comment:

  /**
   * Represents comment depth. Used for rendition purposes. A depth of 0 
   * represents a top-level comment on some post.
   */
  opaque type Depth = Int

  object Depth:

    /**
     * Instantiate a new [[Depth]], forcing a minimum value of 0.
     *
     * @param value The value, rounded up to 0 if negative.
     * @return The new [[Depth]].
     */
    def apply(value: Int): Depth =
      if value < 0 then 0 else value

    given CanEqual[Depth, Depth] = CanEqual.derived

    extension (depth: Depth)
      /**
       * @return The integer value of this depth.
       */
      def toInt(): Int = depth

      /**
       * @return New [[Depth]] that is one level deeper.
       */
      def increment(): Depth = toInt() + 1

  end Depth

end Comment
