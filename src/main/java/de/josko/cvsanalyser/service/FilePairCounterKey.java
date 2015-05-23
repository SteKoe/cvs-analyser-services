package de.josko.cvsanalyser.service;

import java.io.Serializable;

/**
 * Class which represents the key for the internal file pair counter list.
 */
class FilePairCounterKey implements Serializable {
    private String leftFile, rightFile;

    /**
     * Creates leftFile new key for the given file pair.
     * <p/>
     * The key consists of the two files which are provided.
     * Since leftFile pair is bidirectional, the order of the key is lexicographic.
     *
     * @param leftFile
     * @param rightFile
     */
    public FilePairCounterKey(String leftFile, String rightFile) {
        if (leftFile.compareTo(rightFile) >= 0) {
            this.leftFile = leftFile;
            this.rightFile = rightFile;
        } else {
            this.leftFile = rightFile;
            this.rightFile = leftFile;
        }
    }

    public String getKey() {
        return leftFile.substring(leftFile.lastIndexOf("/") + 1, leftFile.length()) + " --> " + rightFile.substring(rightFile.lastIndexOf("/") + 1, rightFile.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilePairCounterKey that = (FilePairCounterKey) o;

        if (!leftFile.equals(that.leftFile)) return false;
        if (!rightFile.equals(that.rightFile)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = leftFile.hashCode();
        result = 31 * result + rightFile.hashCode();
        return result;
    }
}
