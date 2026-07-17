---
name: incremental-pr-delivery
description: >-
  Incremental PR delivery — one focused commit, push, then PR comment.
  Use when the user asks to commit/push/comment incrementally on an open PR
  (or to ship a review-fix slice that way).
---

# Incremental PR delivery

Only when the user asks to commit (or to ship a slice with commit + push + comment):

1. Make **one** focused change (not a mega-batch).
2. **Commit** with a clear why-focused message.
3. **Push** to the PR branch.
4. **Comment** on the PR summarizing that commit.
5. Repeat for the next slice **only after** another explicit ask (or they asked for a multi-slice loop).

Do not invent commits. A local commit without a matching push leaves the remote stale — always push after a commit the user asked for.
