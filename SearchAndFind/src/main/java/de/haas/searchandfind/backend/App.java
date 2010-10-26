package de.haas.searchandfind.backend;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * Hello world!
 *
 */
public class App {

    private static final File targetDirectory = new File("/home/laga/searchandfind/files");

    private static final File indexDirectory = new File("/home/laga/searchandfind/index");

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        // where we store the index on disk
        Directory directory = new SimpleFSDirectory(indexDirectory);

        Thread indexer = new Indexer(targetDirectory, directory);
        indexer.start();

    }
}
