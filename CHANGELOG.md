<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Cloth-jetbrains Changelog

## [Unreleased]

### Added

- Cloth file recognition for `.co`, `.cloth`, and `.cl` files.
- Incremental syntax highlighting with configurable color settings.
- Lexer coverage for comments, literals and escapes, numeric suffixes, declarations, calls, properties, and operators.
- Import paths remain unstyled, while primitive types use a distinct keyword-derived color.
- Cloth `src` directories use the IDE's source-root folder icon in the Project view.
- Cloth files use class, interface, struct, enum, or error icons based on their top-level declaration.

### Removed

- Generated sample tool window.
