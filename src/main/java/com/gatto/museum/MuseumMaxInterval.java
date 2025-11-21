package com.gatto.museum;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * CLI: java MuseumMaxInterval <path-to-input>
 * Input format: lines "HH:MM,HH:MM" (inclusive).
 * Output: lines "<HH:MM>-<HH:MM>;<max>"
 */
public class MuseumMaxInterval {

    private static final int DAY_IN_MINUTES = 24 * 60; // 1440

    public static void main(String[] args) throws IOException {
        List<String> lines;
        if (args.length >= 1) {
            lines = Files.readAllLines(Path.of(args[0]));
        } else {
            try (var in = MuseumMaxInterval.class.getResourceAsStream("/visits.txt")) {
                if (in == null) {
                    System.err.println("Resource Visits.txt not found in classpath");
                    System.exit(1);
                    return;
                }
                lines = new java.io.BufferedReader(new java.io.InputStreamReader(in))
                        .lines().toList();
            }
        }

        // events: how many arrived/left at a specific minute
        var starts = new TreeMap<Integer, Integer>();
        var ends   = new TreeMap<Integer, Integer>();

        int valid = 0;
        for (String raw : lines) {
            var line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }
            var parts = line.split(",");
            if (parts.length != 2) {
                continue;
            }

            int startTime = absoluteTiming(parts[0].trim());
            int endTime = absoluteTiming(parts[1].trim());
            if (startTime < 0 || endTime < 0 || startTime > endTime || endTime >= DAY_IN_MINUTES) {
                continue;
            }

            starts.merge(startTime, 1, Integer::sum);
            ends.merge(endTime, 1, Integer::sum);
            valid++;
        }
        if (valid == 0) {
             System.err.println("Input has no valid records");
            return;
        }

        // all minutes where something changes, just for iteration
        var keys = new TreeSet<Integer>();
        keys.addAll(starts.keySet());
        keys.addAll(ends.keySet());

        var segmentCollector = new SegmentCollector();

        int prevVisitorNumber = 0; // value "after the previous minute" (i.e., accounting for departed)
        Integer prevTime = null;

        for (Integer dayMinute : keys) {
            // interval between events: [prevTime+1 .. dayMinute-1] holds prevVisitorNumber
            if (prevTime != null && dayMinute - (prevTime + 1) > 0) {
                segmentCollector.addSegment(prevTime + 1, dayMinute - 1, prevVisitorNumber);
            }

            int comeAtTime = starts.getOrDefault(dayMinute, 0);
            int leaveAtTime = ends.getOrDefault(dayMinute, 0);

            // first minute dayMinute: include arrivals, but NOT yet subtract departures (inclusivity)
            int curAtT = prevVisitorNumber + comeAtTime;
            segmentCollector.addSegment(dayMinute, dayMinute, curAtT);

            // after minute dayMinute apply departures — this value applies from dayMinute+1
            prevVisitorNumber = curAtT - leaveAtTime;

            prevTime = dayMinute;
        }

        // tail until end of day: [lastKey+1 .. 23:59]
        if (prevTime != null && prevTime < DAY_IN_MINUTES - 1) {
            segmentCollector.addSegment(prevTime + 1, DAY_IN_MINUTES - 1, prevVisitorNumber);
        }

        // printing
        for (Range resultMinutes : segmentCollector.getResult()) {
            System.out.println(formatHourMinute(resultMinutes.start()) + "-" + formatHourMinute(resultMinutes.end()) + ";" + segmentCollector.getMax());
        }
    }

    // --- utilities ---
    static int absoluteTiming(String hhmm) {
        var timeString = hhmm.split(":");
        if (timeString.length != 2) {
            return -1;
        }
        try {
            int hour = Integer.parseInt(timeString[0]);
            int minute = Integer.parseInt(timeString[1]);
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                return -1;
            }
            return hour * 60 + minute;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static String formatHourMinute(int minute) {
        int hour = minute / 60;
        int min = minute % 60;
        return String.format("%02d:%02d", hour, min);
    }
}
