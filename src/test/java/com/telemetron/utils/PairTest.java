package com.telemetron.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PairTest {

    @Test
    public void testEqualsTrue() {
        Pair<String, String> p1 = new Pair<>("1", "2");
        Pair<String, String> p2 = new Pair<>("1", "2");
        assertTrue(p1.equals(p2));
    }

    @SuppressWarnings("all")
    @Test
    public void testEqualsTrueSameObject() {
        Pair<String, String> p1 = new Pair<>("1", "2");
        assertTrue(p1.equals(p1));
    }

    @Test
    public void testEqualsLeftFalse() {
        Pair<String, String> p1 = new Pair<>("1", "2");
        Pair<String, String> p2 = new Pair<>("1", "3");
        assertFalse(p1.equals(p2));
    }

    @Test
    public void testEqualsRightFalse() {
        Pair<String, String> p1 = new Pair<>("1", "2");
        Pair<String, String> p2 = new Pair<>("2", "2");
        assertFalse(p1.equals(p2));
    }

    @SuppressWarnings("all")
    @Test
    public void testNullEqualsFalse() {
        Pair<String, String> p1 = new Pair<>("1", "2");
        assertFalse(p1.equals(null));
    }

    @SuppressWarnings("all")
    @Test
    public void testEqualsFalseWrongObjectType() {
        Pair<String, String> p1 = new Pair<>("1", "2");
        String bad = "I'm not a pair";
        assertFalse(p1.equals(bad));
    }

    @Test
    public void testSameObjectSameHashCode() {
        Pair<String, String> p1 = new Pair<>("1", "2");
        assertEquals(p1.hashCode(), p1.hashCode());
    }

    @Test
    public void testEqualObjectsSameHashCode() {
        Pair<String, String> p1 = new Pair<>("1", "2");
        Pair<String, String> p2 = new Pair<>("1", "2");
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testToStringContainsBothElements() {
        String p = new Pair<>("xxx", "yyy").toString();

        assertTrue(p.contains("xxx"));
        assertTrue(p.contains("yyy"));
    }
}