---
name: seedu-java-code-quality
description: Review or improve Java production and test code using SE-EDU conventions, error handling, SLAP, and course code-quality guidelines.
---

# Seedu Java Code Quality

Use this skill for Java code-quality reviews, PR reviews, and requested quality improvements. Apply it to production and test code within the requested scope. A review request is read-only; edit only when the user requests implementation or fixes. Do not expand a focused change into a repository-wide cleanup.

## Project context and companion skills

Read the target repository's applicable `AGENTS.md`, build configuration, and relevant contracts first. Explicit user instructions and project-specific conventions take precedence over these reusable defaults.

Apply the target project's `seedu-java-style-standard` skill when available, following its coding and JavaDoc companions. Otherwise, read the sibling [Java style skill](../seedu-java-style-standard/SKILL.md). If a companion is unavailable, report that limitation and assess the applicable source conventions directly; do not claim the missing review was completed.

Use the project's required JDK, lint tasks, and testing requirements. Do not assume Java 25, Gradle, JUnit, or a coverage percentage across projects. Do not add tools or dependencies merely to conduct a review.

## Sources and applicability

Use these primary sources, verified on October 25, 2026. Read the relevant sections when interpreting a rule, resolving ambiguity, or reviewing a topic not covered by the companions. Do not rely on this checklist as an exhaustive replacement for the intermediate standard.

- [SE-EDU Java coding standard: basic and intermediate](https://se-education.org/guides/conventions/java/intermediate.html).
- [AY2627-S1 tP grading: code quality](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-grading.html).
- [Course error-handling chapter](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/errorHandling.html).
- [Course code-quality guidelines](https://nus-cs2103-ay2627-s1.github.io/website/book/codeQuality/).
- [SLAP explanation](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html#slap-hard).

The grading rubric is course-specific. In the relevant tP, assess evidence of logging, exceptions, assertions, defensive coding, conventions, SLAP, and duplication control across the reviewed contribution. Elsewhere, apply the practices as quality guidance under the target project's rules. Separate course criteria, course recommendations, repository requirements, and optional engineering suggestions. Do not invent quotas or require every technique in every class or method. Existing code can provide meaningful evidence; decorative additions do not improve quality.

## Manual coding-standard review

Use the companion skills for formatting and JavaDoc, and manually inspect semantics that Checkstyle cannot establish:

- Boolean variables, parameters, fields, and methods should read as true-or-false questions. Check whether names describe the actual condition, rather than merely checking an `is` or `has` prefix. Respect required overrides and external API signatures.
- Check noun/verb choices, collection plurals, acronym casing, descriptive names, and related constants' common prefixes. Reserve short iterator names for small scopes, with `j` and `k` for nested loops.
- Check scope and initialization: avoid invented placeholder values, publicly exposed mutable state, and misleading names. Identify source exceptions separately from stricter project rules, including documentation exceptions for tests, accessors, and exact overrides.

A clean Checkstyle result establishes only the checks that actually ran. It does not prove semantic naming, documentation accuracy, behavior, or maintainability.

## Error handling and defensive coding

- **Logging:** Look for useful diagnostic events, appropriate levels, and enough context to investigate failures. Reuse the project's logging facility. Flag noise, repeated reporting of the same failure, and unnecessary disclosure of contact or other sensitive data when supported by the code.
- **Exceptions:** Trace unusual user/environment failures to a handler that can respond meaningfully. Preserve useful causes when wrapping. Avoid using exceptions for ordinary branching, swallowing failures, and unjustifiably broad catches.
- **Assertions:** Use Java `assert` for internal assumptions and invariants. Assertions can be disabled, so they must have no required side effects and must not replace validation of external input. Distinguish Java assertions from JUnit assertions; verify assertion settings when testing invariant checks.
- **Defensive coding:** Check input boundaries and required relationships. Protect mutable internals where callers could corrupt state. Keep validation proportionate to the contract; avoid redundant checks that obscure it.

For each category, record relevant evidence, an actionable gap, or why it is outside scope. Missing evidence in a small diff does not establish a repository-wide absence. Do not silently alter established error messages, validation order, or recovery behavior during cleanup.

## Readability and maintainability

- **SLAP:** Keep a method's steps at a coherent abstraction level. Extract meaningful operations where orchestration obscures implementation detail. Around 30 lines or deep nesting prompts inspection, not automatic rejection; clearly separated logical blocks can remain readable.
- **Duplication:** Compare similar production and test blocks, especially copy-paste variants. Suggest shared helpers, fixtures, or supported parameterized tests only when they clarify the shared intent. Preserve visible test scenarios and avoid speculative abstractions.
- **Expressions and literals:** Simplify complicated conditions and negations; name meaningful intermediate values. Explain domain literals with named constants where useful.
- **Structure:** Group related work, make the normal path clear, and prefer simple control flow. Check unexpected cases instead of treating a final option as an unconditional default.
- **Names and state:** Avoid ambiguous names, unused parameters, reassignment before use, and recycling variables or parameters for unrelated purposes. Minimize scope and global state.
- **Cleanup:** Identify dead code and unexplained empty catches. Avoid cleverness and premature optimization. Keep comments useful to readers; improve unclear code instead of narrating it.

Explain the concrete readability or maintenance cost before recommending a refactor. A guideline is not a reason to extract every small block or rewrite sound code to personal taste.

## Validation and handoff

Inspect callers, surrounding contracts, and similar tests as needed to substantiate findings. In a diff review, distinguish introduced problems from pre-existing ones; a broader audit can report both with clear labels.

For requested fixes, preserve behavior unless a change is authorized, review relevant tests, and run the target project's required lint and tests. For reviews, run relevant existing checks when practical and report limitations. Do not weaken lint configuration or add suppressions merely to obtain a pass.

Report actionable findings by severity, with an exact file/line, the applicable rule or guideline, impact, and the smallest useful correction. Distinguish required corrections from optional improvements. If no actionable findings exist, say so without manufacturing issues.

Finish with a compact coverage summary for conventions, logging, exceptions, assertions, defensive coding, SLAP, duplication, and other readability guidelines, including evidence and any unreviewed areas. List actual validation commands and results; distinguish automated checks, manual inspection, environment failures, and pre-existing failures. Never claim that tests, Checkstyle, or this review guarantee code quality or a grade. Do not commit, push, or post review comments externally without user authorization.
