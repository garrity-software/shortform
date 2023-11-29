package gs.shortform.model

import gs.uuid.UUID
import gs.shortform.crypto.Hash

/**
  * Represents a top level piece of content - an article, essay, prompt, or some
  * other written piece of work. All content must have a [[Title]] for display
  * purposes.
  *
  * Content cannot currently be edited.
  *
  * @param externalId Globally unique identifier for this content.
  * @param createdAt Instant this content was created.
  * @param createdBy User who created this content.
  * @param title Display title for this content.
  * @param hash Hash of the primary rendered file for this content.
  */
case class Content(
  externalId: UUID,
  createdAt: CreatedAt,
  createdBy: Username,
  title: Title, 
  hash: Hash
)
