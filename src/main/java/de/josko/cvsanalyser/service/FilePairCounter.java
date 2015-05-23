package de.josko.cvsanalyser.service;

import java.util.*;

/**
 * This class counts how often two files have been changed in combination.
 */
class FilePairCounter {
    Map<FilePairCounterKey, Integer> counter = new HashMap<>();

    /**
     * Adds leftFile new file pair to the list of file pairs.
     * If the file pair exists already, the counter for the pair will be increased by 1.
     * If the file pair does not yet exist, the counter will be set to 1.
     *
     * @param leftFile
     * @param rightFile
     */
    public void add(String leftFile, String rightFile) {
        FilePairCounterKey key = obtainKey(leftFile, rightFile);
        if (counter.keySet().contains(key)) {
            int curVal = counter.get(key);
            counter.put(key, curVal + 1);
        } else {
            counter.put(key, 1);
        }
    }

    private FilePairCounterKey obtainKey(String a, String b) {
        return new FilePairCounterKey(a, b);
    }

    /**
     * Returns the counter's value for the given file pair.
     *
     * @param leftFile
     * @param rightFile
     * @return The counter's value for the given file pair, 0 otherwise.
     */
    public int getCounter(String leftFile, String rightFile) {
        FilePairCounterKey lookupKey = new FilePairCounterKey(leftFile, rightFile);

        Optional<FilePairCounterKey> first = counter.keySet().stream().filter(key -> key.equals(lookupKey)).findFirst();
        if (first.isPresent()) {
            return counter.get(first.get());
        } else {
            return 0;
        }
    }

    public Map<FilePairCounterKey, Integer> getList() {
        List list = new LinkedList(counter.entrySet());

        Collections.sort(list, (o1, o2) -> ((Comparable) ((Map.Entry) (o2)).getValue())
                .compareTo(((Map.Entry) (o1)).getValue()));

        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
