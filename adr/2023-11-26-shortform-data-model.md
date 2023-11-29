# ADR 2023-11-26: ShortForm Data Model

## Table of Contents

- [Context](#context)
- [Decision](#decision)
- [Relational Data Model](#relational-data-model)
    - [Table: `content`](#table-content)
    - [Table: `comments`](#table-comments)
    - [Table: `assets`](#table-assets)
    - [Table: `tags`](#table-tags)
    - [Table: `content_tags`](#table-content_tags)
    - [Table: `asset_tags`](#table-asset_tags)
- [Disk Data Model](#disk-data-model)

## Context

ShortForm is a project intended to help facilitate discussion of interesting 
topics among groups of people. Some person posts a prompt, essay, article, or
some other piece of content and others engage in discussion. While most content
is textual, ShortForm supports some set of static assets as well. Note that
ShortForm only allows assets to be uploaded and presented by approved posters.
Comments may only include text.

## Decision

- Adopt a relational data model that references content stored in some static 
  file storage mechanism such as a local disk or S3.
- Support comments.
- ShortForm must support multiple backing storage mechanisms.
- Support arbitrary file upload and download.

## Relational Data Model

- [Table: `posts`](#table-posts)
- [Table: `comments`](#table-comments)
- [Table: `assets`](#table-assets)
- [Table: `tags`](#table-tags)
- [Table: `post_tags`](#table-post_tags)
- [Table: `asset_tags`](#table-asset_tags)

### Table: `posts`

```sql
CREATE TABLE posts(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL,
    created_by BIGINT NOT NULL,
    title TEXT NOT NULL,
    hash TEXT NOT NULL
);
```

### Table: `comments`

```sql
CREATE TABLE comments(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id UUID NOT NULL UNIQUE,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by BIGINT NOT NULL,
    contents TEXT NOT NULL,
    depth INT NOT NULL,
    parent BIGINT NULL,
);
```

Comments store their content directly. Comments are not complex, and are 
typically shorter than posts. The practical limit for PostgreSQL is 1gb per row, 
but ShortForm imposes a configurable limit (10kb by default).

### Table: `assets`

```sql
CREATE TABLE assets(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    external_id UUID NOT NULL UNIQUE,
    title TEXT NOT NULL,
    extension TEXT NOT NULL,
    hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by BIGINT NOT NULL
);
```

### Table: `tags`

```sql
CREATE TABLE tags(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    value TEXT NOT NULL UNIQUE
);
```

Tags are arbitrary labels that authors may assign to top level posts and assets.

### Table: `post_tags`

```sql
CREATE TABLE post_tags(
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY(post_id, tag_id)
);
```

### Table: `asset_tags`

```sql
CREATE TABLE asset_tags(
    asset_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY(asset_id, tag_id)
)
```

## Disk Data Model

- The directory structure (path structure) is based on year, month, day.
- Within each day, one file exists per piece of content created on that day.

**Example**:

```
/2023/11/26/0e61ae14cc1f444cb5cbe48f0c301193.md
/2023/11/26/0e61ae14cc1f444cb5cbe48f0c301193.html
/2023/11/26/99ebf96760ba498798652ee4140a9f3e.png
```
