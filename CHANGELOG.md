# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v1.0.5-1.16.5] - 2021-06-29
### Changed
- Switched string option builder to generic builder
- Removed a bunch of unneeded annotations

## [v1.0.4-1.16.5] - 2021-06-27
### Changed
- Added more console output while mod elements are being registered and loaded
### Fixed
- Fixed concurrency issues when loading mod elements

## [v1.0.3-1.16.5] - 2021-06-25
### Removed
- Removed some methods which would work without providing a parent mod id when it's really required actually
- Removed `AssetLocations` as it's not needed on a library level for now at least
### Fixed
- Multiple mods can now use Puzzles Lib simultaneously
- Disabled elements are no longer loaded during start-up

## [v1.0.2-1.16.5] - 2021-06-24
### Added
- Elements can now be made persistent, removing the user's ability to disable them

## [v1.0.1-1.16.5] - 2021-06-23
### Changed
- Moved package location to `fuzs.puzzleslib`

## [v1.0-1.16.5] - 2021-06-20
### Added
- Initial release
- Setup CurseGradle for automatic upload to CurseForge

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/