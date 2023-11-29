package gs.shortform.model

import gs.uuid.UUID
import gs.shortform.crypto.Hash

/**
  * Represents a top level piece of content - an article, essay, prompt, or some
  * other written piece of work. All posts must have a [[Title]] for display
  * purposes.
  *
  * Posts cannot currently be edited.
  *
  * @param externalId Globally unique identifier for this post.
  * @param createdAt Instant this post was created.
  * @param createdBy User who created this post.
  * @param title Display title for this post.
  * @param hash Hash of the primary rendered file for this post.
  * @param tags List of [[Tag]] applied to this post.
  */
case class Post(
  externalId: UUID,
  createdAt: CreatedAt,
  createdBy: Username,
  title: Title, 
  hash: Hash,
  tags: List[Tag]
)
