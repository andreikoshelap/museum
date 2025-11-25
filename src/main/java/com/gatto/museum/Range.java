package com.gatto.museum;

record Range(int start, int end) {
    boolean isNeighbourWith(Range other) {
        return this.end + 1 == other.start;
    }
    Range merge(Range other) {
        return new Range(this.start, other.end);
    }
}
