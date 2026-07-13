---
name: incremental-pr-delivery
description: >-
  Incremental PR delivery — one focused commit, push, then PR comment.
  Use when working on an open PR branch, shipping review fixes, or the user asks
  to commit/push/comment incrementally.
---

# Incremental PR delivery

When implementing on an open PR branch:

1. Make **one** focused change (not a mega-batch).
2. **Commit** with a clear why-focused message.
3. **Push** to the PR branch.
4. **Comment** on the PR summarizing that commit.
5. Repeat for the next slice.

Do not leave large piles of unrelated edits uncommitted, and do not squash unrelated review fixes into one silent push. A local commit without a matching push leaves the remote stale — always push after commit.
