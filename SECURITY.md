# Security Policy

## Supported Versions

This is a personal portfolio project. Security updates are applied on a best-effort basis to the latest commit on the `master` branch only.

## Reporting a Vulnerability

If you discover a security issue, please **do not open a public issue.** Instead:

1. Use [GitHub's private vulnerability reporting](https://github.com/HitOdessit/ColorMixLab/security/advisories/new) on this repository, or
2. Open a regular issue with the title `Security: [brief description]` and minimal details, then request a private channel for follow-up.

You can expect an initial response within 7 days. If the issue is confirmed, a fix will be prepared and disclosed coordinated with you.

## Scope

This is an offline single-player game. There are no servers, accounts, or remote APIs. The only persisted data is the local leaderboard (stored in `SharedPreferences` on Android and `NSUserDefaults` on iOS). PII risk is minimal.
