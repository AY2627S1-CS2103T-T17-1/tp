# Project context

This repository is a brownfield Java project based on the existing AB3 address-book application used in an introductory software engineering course in an undergraduate computer science program. Students extend and maintain the existing codebase as part of their team project.

The original team-project repository is https://github.com/NUS-CS2103-AY2627-S1/tp.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.
  * For non-trivial feature requests without an approved implementation-ready specification, suggest using `$specify-before-implementing` and follow its stage gates when invoked. Continue to suggest it in future feature-work chats in this repository.

# Project-specific requirements

## Course constraints

Follow the [AY2627-S1 tP constraints](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-constraints.html), checked 13 October 2026. Read the source before proposing architecture, dependencies, UI, storage, or release changes; this reminder is not exhaustive.

* Evolve AB3 incrementally, delivering working breadth-first increments consistently; use primarily OOP.
* Target one typing-oriented user; no shared-user workflows or routine cross-user data access.
* Store human-editable text locally, supporting manual edits at least as well as AB3; no DBMS.
* Application file access stays inside the JAR's directory and its subdirectories; do not change OS settings.
* Support Windows, Linux, and macOS with only Java 25 installed, without an installer or dependence on our own remote server.
* Third-party software needs prior teaching-team approval; existing class-wide approval suffices. Verify forum evidence before adding dependencies. Libraries must be free, open-source, permissively licensed, require no user installation, and respect other constraints. Services need not be open-source.
* GUI: work well at 1920x1080+ with 100%/125% scaling; remain usable at 1280x720+ with 150% scaling.
* Deliver one JAR, or one ZIP if necessary; maximum 100 MB, without unnecessary assets/dependencies.
* Recommendations: CLI-first input, testability, realistic scope, minimal network dependence/fallbacks; obtain NUS IT approval for NUS data use.

Distinguish course requirements, course recommendations, and our own engineering suggestions. A textbook tool mention is not evidence of third-party approval. Distinguish application runtime dependencies from development-only tooling; check applicability rather than assuming either is exempt or prohibited. Do not post approval requests without the user's authorization.

CLI-first means users primarily operate TutorContacts by typing text commands into its JavaFX command box. It does not require a terminal, console application, or text-only display. The graphical interface provides visual feedback, including contact cards, highlighting, and expansion. Do not equate CLI-first interaction with removing the GUI or replacing commands with mouse-driven forms.

## Shared teamwork responsibilities

Follow the [tP Individual Expectations on Teamwork](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-expectations.html#individual-expectations-on-teamwork), checked 13 October 2026. Each member is expected to do a non-trivial share of team tasks, carry project roles and responsibilities, and review teammates' work. Each important role should still have one clear owner; sharing work does not mean making everyone responsible for everything.

For our workflow, distribute repository administration, version-control coordination, issue tracking, PR reviews, integration, and release work across the team. These are practical applications of the course expectations, not a requirement that every member perform every task. Keep contributions and responsibility visible through assigned issues and reviewed PRs. Have a teammate review shared guidance changes before merging them so the team can agree on the working context.

## Java coding standard

All Java code in this project, including tests and future changes, must follow the project-specific `seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. Read and apply that skill before editing Java. In particular, use lowercase packages, PascalCase class names, camelCase methods and variables, SCREAMING_SNAKE_CASE constants, four-space indentation, K&R braces, explicit imports, a 120-character hard line limit, braces around all control-flow bodies, and descriptive public JavaDoc using American English.

## Java code quality

All Java production and test code must follow the project-specific `seedu-java-code-quality` skill at `.codex/skills/seedu-java-code-quality/SKILL.md`. Read and apply it when editing Java or reviewing Java changes, alongside the existing coding and JavaDoc standards. Review logging, exceptions, assertions, defensive coding, SLAP, duplication (especially in tests), and the course code-quality guidelines. Use manual review as well as Checkstyle, and apply the same expectations across the codebase. Keep cleanup within the requested scope; do not add techniques merely to satisfy a checklist. Have a teammate review this shared guidance and skill before merging.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit test coverage:

Maintain JUnit tests for approximately the top 50% of methods, prioritizing complex, core, or business-critical logic. After every code change, review and update the relevant JUnit tests so they remain aligned with the implementation and continue to meet this coverage target.

## Git

All future commits and branch names must follow the project-specific `seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`, based on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Read and apply it before preparing a commit. Commit subjects must be imperative, capitalized, concise, and have no trailing period; non-trivial commits need a 72-column body explaining what and why; branch names must be meaningful kebab-case names, using the issue-number format when applicable.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
