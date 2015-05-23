package de.josko.cvsanalyser.service;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FilePairCounterTest {

    @Test
    public void existingFilePairCounter() throws Exception {
        FilePairCounter counter = new FilePairCounter();
        counter.add("A", "B");

        assertThat(counter.getCounter("A", "B"), is(1));
        assertThat(counter.getCounter("B", "A"), is(1));
    }

    @Test
    public void nonExistingPairCounter() throws Exception {
        FilePairCounter counter = new FilePairCounter();
        assertThat(counter.getCounter("A", "B"), is(0));
    }

    @Test
    public void addingSameFilePairIncreasesCounter() throws Exception {
        FilePairCounter counter = new FilePairCounter();
        counter.add("A", "B");
        counter.add("A", "B");
        counter.add("B", "A");

        assertThat(counter.getCounter("A", "B"), is(3));
        assertThat(counter.getCounter("B", "A"), is(3));
    }
}
