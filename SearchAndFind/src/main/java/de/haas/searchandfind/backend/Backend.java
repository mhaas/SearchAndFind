package de.haas.searchandfind.backend;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * Starts the backend.
 * Kicks off file indexing. Keep this running in the background to keep the index up to date.
 * 
 *
 */
public class Backend {

    public static final File TARGET_DIRECTORY = new File("/home/laga/searchandfind/files");

    public static final File INDEX_DIRECTORY = new File("/home/laga/searchandfind/index");

    public static final String FIELD_FILE_NAME = "fileName";
    public static final String FIELD_LAST_MODIFIED = "lastModified";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_KEYWORDS = "keywords";


    public static void main(String[] args) throws IOException, InterruptedException {
        // where we store the index on disk
        Directory directory = new SimpleFSDirectory(INDEX_DIRECTORY);

        Indexer indexer = new Indexer(TARGET_DIRECTORY, directory);
        indexer.kickOffIndexing();

    }
}
