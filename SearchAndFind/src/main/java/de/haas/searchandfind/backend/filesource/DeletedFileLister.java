package de.haas.searchandfind.backend.filesource;

import de.haas.searchandfind.backend.Backend;
import de.haas.searchandfind.common.Constants;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.store.Directory;

/**
 * Scans database for files, then checks file system if they still exists.
 * If they don't exist, they're enqueued to be deleted.
 *
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class DeletedFileLister extends Thread {

    private Directory indexDirectory;
    private static final Logger l = Logger.getLogger(DeletedFileLister.class.getName());
    private BlockingQueue<FileWrapper> fileQueue;

    public DeletedFileLister(BlockingQueue<FileWrapper> queue, Directory indexDir) {
        this.fileQueue = queue;
        this.indexDirectory = indexDir;
    }

    @Override
    public void run() {
        try {
            this.checkForDeletedFiles();
        } catch (CorruptIndexException ex) {
            Logger.getLogger(DeletedFileLister.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DeletedFileLister.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void checkForDeletedFiles() throws CorruptIndexException, IOException {

        boolean readOnly = false;
        IndexSearcher searcher = new IndexSearcher(this.indexDirectory, readOnly);

        int max = searcher.maxDoc();
        for (int ii = 0; ii < max; ii++) {
            Document currentDoc = searcher.doc(ii);
            if (currentDoc == null) {
                continue;
            }
            String pathName = currentDoc.get(Constants.FIELD_FILE_NAME);
            if (pathName == null) {
                l.severe("No pathname for document " + ii);
            }
            File currentFile = new File(pathName);
            if (!currentFile.exists() || currentFile.isDirectory()) {
                l.info("Deleted document for file " + pathName);
                FileWrapper currentWrappedFile = new FileWrapper(currentFile, FileWrapper.FileState.FILE_DELETED);
                try {
                    this.fileQueue.put(currentWrappedFile);
                    // TODO: enqueue FileWrapper instance into Queue?
                } catch (InterruptedException ex) {
                    Logger.getLogger(DeletedFileLister.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        searcher.close();
    }
}
