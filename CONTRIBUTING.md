# CONTRIBUTING

## How to release

### Prerequisites
- Install [sdkman](https://sdkman.io/install)
- Install latest scala, e.g. `sdk install scala 3.6.2`

### Generate release notes

- Bump the version in `version.sbt`
- Run `git tag v<NEW_VERSION>` (e.g., `git tag v0.42.3`)
- release notes with `scala bin/generate-release.scala -- release-notes -s v<OLD_VERSION> -e v<NEW_VERSION> --token GITHUB_TOKEN` (e.g., `scala bin/generate-release.scala -- release-notes -s v0.42.2 -e v0.42.3 --token GITHUB_TOKEN`).

