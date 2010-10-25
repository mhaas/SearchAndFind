package de.haas.searchandfind;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class Indexer {

    private static final File DIRECTORY = new File("/home/laga/uni/");
    private static final File INDEX = new File("/tmp/lucene/");
    // TODO: sensible value
    private static final int MAX_FIELD_LENGTH = 500;
    private BlockingQueue<File> queue = new LinkedBlockingQueue<File>();
    // TODO: is an IndexWriter threadsafe? Or do we need a singleton here?
    // "An IndexWriter creates and maintains an index. "

    private void kickOffIndexing() throws IOException, InterruptedException {

        // where we store the index on disk
        Directory directory = new SimpleFSDirectory(INDEX);
        // this is used to analyze/tokenize fields
        // apparently, it's not used for all fields
        // TODO: read up on that
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
        IndexWriter.MaxFieldLength mfl = new IndexWriter.MaxFieldLength(MAX_FIELD_LENGTH);
        IndexWriter writer = new IndexWriter(directory, analyzer, mfl);



        FileLister f = new FileLister(this.queue, Indexer.DIRECTORY);
        f.start();
        // loop endlessly.
        // possibly add support for poison element in queue later on
        // OTOH, it's quite possible we want to keep indexing with the live
        // indexer forever and ever
        while (true) {
            File file = this.queue.take();
            Document document = this.dispatchToDocumentGenerator(file);
            writer.addDocument(document);
        }
    }

    /**
     * Invokes
     *
     * @param f
     * @return
     */
    private Document dispatchToDocumentGenerator(File f) {
        return null;
    }
}
