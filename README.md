## Reporter of user commits in given time

### Requirements
- scala
- scala-cli (https://scala-cli.virtuslab.org/install/)

### Usage

**Help:**

`./Reporter.scala -h`

```text
 ---- Required arguments ---- 

 -u,     --user          User email address (e.g. --user trombka@touk.pl)
 -s,     --since         Date to start gathering commits from (--since in git). This date is inclusive (e.g. --since 2024-01-14)
 -b,     --before        Date to end gathering commits to (--before in git). This date is inclusive (e.g. --before 2024-01-30)

 ---- Optional arguments ---- 

 -h,     --help          Help! (e.g. --help )
 -nm,    --no-merges     Don't count merges (e.g. --no-merges '--no-merges')
 -p,     --pretty        How should be commits displayed (check out git docs for symbols meaning) (e.g. --pretty '%H - %cd - %s')
```

**Example usage:**

`./Reporter.scala -u user@domain.com -s '2024-01-14' -b '2024-01-30'`

```text
---- [~/Work/trubo-application] ----
8fa98573aeba0354bd1d326sf2da6d2a8e8d7d01 - Restore previous version...
9d93c5k1eb9a4726084754dc3a5305d84d51019e - Fix, sorry, my bad
1d78f5083f7046173616cd9c3119baf8bf61c90f - That will be awesome
```
