# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

- UI optimization: Change position of '0' on the key pad to only be positioned as the last number if zer is the first and lowest possible number. This retains 1, 2, 3 as the first row of numbers.
- UI optimization: Fix padding of the grid on the new game screen.

### Deprecated

### Removed

### Fixed

- Fix behavior of the "undo" option while displaying the errors to behave completly the same as use the undo button of the app bar.

### Security

## [0.12.1] - 2023-11-18

As Google play refuses to accept this version code after an aborted creation of a release, this is technically the same as 0.12.0 with an increased versin code and version number.

## [0.12.0] - 2023-11-18 [YANKED]

### Added

- Add German localization.

## [0.11.0] - 2023-11-18

### Added
//test

- Add monochrome launcher icon to be use with e.g. Android 13 and Themed Icons.

### Fixed

- If there are two values entered in one row or column, only count the wrong value as a single mistake.
- When revealing the value of a cell, the possible numbers of this cell are now cleared.
- Revealing of selected cell or selected cage was not working reliable.

## [0.10.1] - 2023-11-12

### Fixed

- Fix bug where the difficulty of game variants may not be chosen even thought the difficulty is known of the game variant.
- Fix bug where a restart of a game does not clear the possible numbers.
- Fix layout bug where the main screen and other screens were using a tablet like layout but being modern smartphones having >480dp wide displays.

## [0.10.0] - 2023-11-06

Bumps used Java version from 1.8 to 11 as needed by Material Drawer from Mike Penz.

### Added

- Show drawing 'The Siesta' from Paul Gauguin when opening the navigation drawer.

### Changed

- Use third party navigation drawer, enabling badges to display the number of saved games.

### Deprecated

### Removed

### Fixed

- Fix new preference values which were ignored. From now on, dark mode gets activated and the settings of possible numbers are changed.
- Fix initial help dialog was not showing up.

### Security

## [0.9.4] - 2023-10-31

### Changed

- Update forgotten version number to 0.9.4 and version code to 4

## [0.9.3] - 2023-10-31

### Security

- Use matching version of Gradle wrapper jar and ensure that the wrapper in use matches the SHA256 hash of the original one. 

## [0.9.2] - 2023-10-30

### Added

- Add changelog.

## [0.9.1] - 2023-10-29

### Added

- Add a privacy policy demanded by the Google play console.

### Removed

- Remove running SonarQube as it breaks the F-Droid build. Will be re-enabled soon.

## [0.9.0] - 2023-10-29

Initial release.

Main differences to HoloKenMod 1.6.1, from which this project was forked:
- Adds more grid options and more flexible grid sizes, including non-square grids
- Migrates code from Java to Kotlin
- Uses Android Material Components
- Overhauls complete UI overhaul, using theme based on a picture from Paul Gauguin
