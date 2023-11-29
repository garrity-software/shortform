# ADR 2023-11-27: ShortForm User Model

## Table of Contents

- [Context](#context)
- [Decision](#decision)

## Context

ShortForm is a project intended to help facilitate discussion of interesting 
topics among groups of people. Some person posts a prompt, essay, article, or
some other piece of content and others engage in discussion.

This application depends on the concept of a _user_. ShortForm has users who are
allowed to post articles and upload assets, as well as the more-numerous user
category which is allowed to post comments on content. Comments are the primary
discussion forum of ShortForm.

## Decision

Keep scope tight for the initial build of ShortForm.

- Support two types of users: posters and commenters.
- Do not support registration - administrators in control of the server must add
  users by hand.
- Users have a username and a password.
- New accounts cannot be used until users supply a password.
- New accounts receive their initial password via the password reset mechanism.
- The password reset mechanism for an account must be initiated by an admin.
- Manage user accounts in the ShortForm database.
- Use Argon2 for password encryption.

These restrictions allow ShortForm to work as intended while remaining light on
features initially, serving a small community that communicates directly outside
of ShortForm.

## Relational Data Model

### Enum Type: `user_role`

```sql
CREATE TYPE user_role AS ENUM ('poster', 'commenter');
```

### Enum Type: `user_status`

```sql
CREATE TYPE user_status AS ENUM ('active', 'locked', 'initializing');
```

### Table: `users`

```sql
CREATE TABLE users(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role user_role NOT NULL,
    status user_status NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);
```

### Table: `password_resets`

```sql
CREATE TABLE password_resets(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    token TEXT NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL
);
```
