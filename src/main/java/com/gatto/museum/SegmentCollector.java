package com.gatto.museum;

import java.util.ArrayList;
import java.util.List;

    final class SegmentCollector {
        private int max = 0;
        private final List<Range> result = new ArrayList<>();

        void addSegment(int segmentStart, int segmentEnd, int visitorsNumber) {
            if (segmentStart > segmentEnd || visitorsNumber <= 0) {
                return;
            }
            if (visitorsNumber > max) {
                max = visitorsNumber;
                result.clear();
                result.add(new Range(segmentStart, segmentEnd));
                return;
            }
            if (visitorsNumber == max) {
                if (!result.isEmpty()) {
                    Range last = result.get(result.size() - 1);
                    if (last.isAdjacentTo(new Range(segmentStart, segmentEnd))) {
                        result.set(result.size() - 1, last.merge(new Range(segmentStart, segmentEnd)));
                        return;
                    }
                }
                result.add(new Range(segmentStart, segmentEnd));
            }

        }

        public int getMax() {
            return max;
        }

        public List<Range> getResult()  {
            return result;
        }
    }
