package de.haas.searchandfind.backend;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import de.haas.searchandfind.common.Constants;

/**
 * Starts the backend.
 * Kicks off file indexing. Keep this running in the background to keep the index up to date.
 * 
 *
 */
public class Backend {

    /**
     * Prints usage information to stderr.
     */
    private static void showHelpOnStderr() {
        System.err.println("Welcome to " + Constants.NAME + " Version " + Constants.VERSION);
        System.err.println("First argument must be the directory containing the files to be indexed");
        System.err.println("Second argument must be directory containing the index");
    }

    /**
     * Starts indexing.
     * 
     */
    public static void main(String[] args) throws IOException, InterruptedException {


        if (args.length < 2) {
            showHelpOnStderr();
            System.exit(1);
        }

        File target = new File(args[0]);
        File index = new File(args[1]);

        // where we store the index on disk
        Directory directory = new SimpleFSDirectory(index);

        Indexer indexer = new Indexer(target, directory);
        indexer.kickOffIndexing();

    }
}
