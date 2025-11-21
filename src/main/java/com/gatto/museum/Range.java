package com.gatto.museum;

record Range(int start, int end) {
    boolean isAdjacentTo(Range other) {
        return this.end + 1 == other.start;
    }
    Range merge(Range other) {
        return new Range(this.start, other.end);
    }
}
