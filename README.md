## Museum Max Visitors — Difference/Prefix Version

Find all inclusive time intervals during a day when the number of visitors in the museum is maximal, given minute precision.
Inclusivity rule: if one visitor arrives at 10:30 and another leaves at 10:30, both count as inside at 10:30.

## Problem

Input is a text file, each line contains an entrance and a leaving time:
---
HH:MM,HH:MM
---
Times are in 24-hour format. The first time is the entrance time, the second is the leaving time.
Lines may be in any order. Times are minute-precision, 24-hour format, both endpoints inclusive.

Output: print every maximal interval (inclusive) where the concurrent visitor count is the global maximum, one per line:

---
<HH:MM>-<HH:MM>;<maxCount>
---

## Algorithm (Difference Array + Prefix Sum)

Model the day as minutes 0..1439 (00:00..23:59).
For each inclusive visit [start,end]:
Set all visits into TreeMap. Then iterate over the map to find max intervals.

Complexity: O(N + 1440) time, O(1441) space.

##  Build & Run

---
javac MuseumMaxInterval.java
java MuseumMaxInterval
---
