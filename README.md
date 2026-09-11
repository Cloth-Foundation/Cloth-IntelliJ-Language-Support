# Cloth for JetBrains IDEs

Language support for [Cloth](https://cloth.dev) in IntelliJ IDEA and other JetBrains IDEs.

## Current features

- Recognizes `.co`, `.cloth`, and `.cl` source files.
- Highlights Cloth keywords, modifiers, primitive and named types, literals, comments, operators, declarations, calls, properties, and built-in functions.
- Highlights valid and invalid string/character escapes independently.
- Provides a configurable **Cloth** page under **Settings | Editor | Color Scheme**.
- Shows Cloth `src` directories with the IDE's source-root folder icon.
- Shows distinct file icons for classes, interfaces, structs, enums, and errors.
- Supports line/block comment toggling, matching delimiters, and automatic quote/delimiter pairing.
- Uses an incremental lexer designed to be reused by future parser, PSI, and LSP work.

## Development

Run the tests:

```shell
./gradlew test
```

Launch a development IDE with the plugin installed:

```shell
./gradlew runIde
```

Build the installable plugin archive:

```shell
./gradlew buildPlugin
```

On Windows, use `gradlew.bat` instead of `./gradlew`.
