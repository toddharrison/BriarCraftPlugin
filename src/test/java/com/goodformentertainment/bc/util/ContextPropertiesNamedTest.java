package com.goodformentertainment.bc.util;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

import com.goodformentertainment.bc.util.wrap.Named;

public class ContextPropertiesNamedTest {
    private ContextProperties<Named<W>, L> props;

    @Before
    public void init() {
        props = new ContextProperties<Named<W>, L>();
    }

    @Test
    public void testContext() {
        final Named<W> w = Named.wrap("test1", new W());
        final L l1 = new L();
        final L l2 = new L();

        assertNull(props.set(w, l1));
        assertEquals(l1, props.get(w));
        assertEquals(l1, props.set(w, l2));
        assertEquals(l2, props.get(w));

        assertNull(props.get(Named.wrap("test2", new W())));
    }

    @Test
    public void testContextProperty() {
        final Named<W> w = Named.wrap("test1", new W());
        final L l1 = new L();
        final L l2 = new L();
        final String prop = "prop1";

        assertNull(props.set(w, prop, l1));
        assertEquals(l1, props.get(w, prop));
        assertEquals(l1, props.set(w, prop, l2));
        assertEquals(l2, props.get(w, prop));

        assertNull(props.get(w));
        assertNull(props.get(w, "foo"));
    }

    @Test
    public void testContextIterator() {
        final Named<W> w1 = Named.wrap("test1", new W());
        final Named<W> w2 = Named.wrap("test2", new W());
        final Named<W> w3 = Named.wrap("test3", new W());
        final Named<W> w4 = Named.wrap("test4", new W());
        final Named<W> w5 = Named.wrap("test5", new W());
        final L l = new L();

        assertTrue(props.contexts().isEmpty());

        assertNull(props.set(w5, l));
        assertNull(props.set(w1, l));
        assertNull(props.set(w3, l));
        assertNull(props.set(w4, l));
        assertNull(props.set(w2, l));

        final Queue<Named<W>> queue = new LinkedList<Named<W>>();
        queue.add(w1);
        queue.add(w2);
        queue.add(w3);
        queue.add(w4);
        queue.add(w5);
        for (final Named<W> w : props.contexts()) {
            assertEquals(queue.remove(), w);
        }
    }

    @Test
    public void testContextPropertiesIterator() {
        final Named<W> w = Named.wrap("test1", new W());
        final L l = new L();

        assertNull(props.set(w, "c", l));
        assertNull(props.set(w, "a", l));
        assertNull(props.set(w, "e", l));
        assertNull(props.set(w, "b", l));
        assertNull(props.set(w, "d", l));

        assertTrue(props.names(Named.wrap("test2", new W())).isEmpty());

        final Queue<String> queue = new LinkedList<String>();
        queue.add("a");
        queue.add("b");
        queue.add("c");
        queue.add("d");
        queue.add("e");
        for (final String name : props.names(w)) {
            assertEquals(queue.remove(), name);
        }
    }

    class W {
    }

    class L {
    }
}
