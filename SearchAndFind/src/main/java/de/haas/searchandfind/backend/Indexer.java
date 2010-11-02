package de.haas.searchandfind.backend;

import de.haas.searchandfind.backend.filesource.FileWrapper;
import de.haas.searchandfind.backend.filesource.FileWatcher;
import de.haas.searchandfind.backend.filesource.NewFileLister;
import de.haas.searchandfind.backend.documentgenerator.DocumentFactory;
import de.haas.searchandfind.backend.filesource.FileWrapper.FileState;
import de.haas.searchandfind.backend.filesource.DeletedFileLister;
import de.haas.searchandfind.common.Constants;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class Indexer {

    // TODO: sensible value
    private static final int MAX_FIELD_LENGTH = 500;
    private BlockingQueue<FileWrapper> queue = new LinkedBlockingQueue<FileWrapper>();
    // TODO: is an IndexWriter threadsafe? Or do we need a singleton here?
    // "An IndexWriter creates and maintains an index. "
    private Directory directory;
    private File targetDir;
    private IndexWriter writer;
    private final static Logger l = Logger.getLogger("Indexer");

    public Indexer(File targetDirectory, Directory indexDirectory) {
        this.targetDir = targetDirectory;
        this.directory = indexDirectory;
    }

    public void kickOffIndexing() throws IOException, InterruptedException {

        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
        IndexWriter.MaxFieldLength mfl = new IndexWriter.MaxFieldLength(MAX_FIELD_LENGTH);
        this.writer = new IndexWriter(this.directory, analyzer, mfl);

        // TODO: this is somewhat race-y. Ideally, we'd start the FileWatcher once
        // all files are read by the NewFileLister. On the other hand, we might miss
        // newly created files in this case.
        // TODO: DeletedFileLister needs to be added here
        // TODO: first call index maintenance (blocking), then
        // call NewFileLister (blocking), then go live with FileWatcher (non-blocking)
        // While this is not as nice, it ensures we do not get weird race conditions

        //DeletedFileLister im = new DeletedFileLister(this.queue, this.directory);
        //im.start();
        l.info("Starting new file lister");
        NewFileLister lister = new NewFileLister(this.queue, this.targetDir);
        lister.start();
        l.info("Starting file watcher");
        FileWatcher watcher = new FileWatcher(this.queue, this.targetDir);
        watcher.start();
        // loop endlessly.
        // possibly add support for poison element in queue later on
        // OTOH, it's quite possible we want to keep indexing with the live
        // indexer forever and ever
        // TODO: filter out known documents

        // commit once
        while (true) {
            //System.out.println(i);
            FileWrapper wrappedFile = this.queue.take();
            l.info("Looking at file " + wrappedFile.getFile().getPath());
            // do we already know this document?
            if (wrappedFile.getState() == FileState.FILE_NEW) {
                if (!this.hasFileBeenUpdated(wrappedFile)) {
                    l.info("Document is listed as NEW, but our index is up to date");
                    continue;
                } else {
                    l.info("Document is listed as NEW and newer than what we have in our index. Deleting old document, processing new Document.");
                    // TODO: thanks to jnotify, we (apparently) get multiple MODIFIED events
                    // which returns in multiple FileWrapper instances with FileState.FILE_NEW
                    // apparently, for these subsequent events, the lastModTime will always
                    // be slightly higher which is why we re-index them
                    // this might be a performance penalty.
                    this.deleteDocumentByPath(wrappedFile.getFile().getPath());
                }
            } else if (wrappedFile.getState() == FileState.FILE_DELETED) {
                String path = wrappedFile.getFile().getPath();
                l.info("File deleted. Removing from index. File: " + path);
                this.deleteDocumentByPath(path);
            }
            File file = wrappedFile.getFile();

            Logger.getLogger(Indexer.class.getName()).log(Level.INFO, "Creating Document for file " + file.getCanonicalPath());
            Document document = DocumentFactory.getDocument(file);
            if (document == null) {
                l.info("Document as returned by Factory is null. Skipping");
                continue;
            } else {
                l.info("Document created successfully");
            }

            writer.addDocument(document);
            writer.commit();
        }
        //writer.close();
    }

    private void deleteDocumentByPath(String path) throws CorruptIndexException, IOException {
        Term t = new Term(Constants.FIELD_FILE_NAME, path);
        this.writer.deleteDocuments(t);
    }

    private boolean hasFileBeenUpdated(FileWrapper file) throws IOException {


        // if we do not have an index yet, then all files are new
        if (!IndexReader.indexExists(this.writer.getDirectory())) {
            return true;
        }
        IndexSearcher searcher = new IndexSearcher(this.writer.getReader());
        Term searchTerm = new Term(Constants.FIELD_FILE_NAME, file.getFile().getPath());
        TermQuery q = new TermQuery(searchTerm);
        TopDocs res = searcher.search(q, 1);
        for (int ii = 0; ii < res.scoreDocs.length; ii++) {
            l.info("Found existing document by path name");
            ScoreDoc current = res.scoreDocs[ii];
            int docNum = current.doc;
            Document d = searcher.doc(docNum);
            if (d == null) {
                l.severe("Document d is null. This is a problem.");
            }
            Field lastMod = d.getField(Constants.FIELD_LAST_MODIFIED);
            long lastModDate = Long.decode(lastMod.stringValue());

            if (file.getFile().lastModified() <= lastModDate) {
                return false;
            }
        }
        return true;
    }
}
