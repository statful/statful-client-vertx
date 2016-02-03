package com.telemetron.utils;

import java.util.Objects;

/**
 * Simple class supporting Pair
 *
 * @param <L> type of the object to store on the left leaf of the pair
 * @param <R> type of the object to store on the right leaf of the pair
 */
public final class Pair<L, R> {

    /**
     * Left element of the Pair
     */
    private L left;
    /**
     * Right element of the Pair
     */
    private R right;

    /**
     * Pair constructor
     *
     * @param left  element of the pair
     * @param right element of the pair
     */
    public Pair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @return gets the value of the left element
     */
    public L getLeft() {
        return left;
    }

    /**
     * @return gets the value of the right element
     */
    public R getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "Pair{" + "left=" + left + ", right=" + right + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left)
                && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}

