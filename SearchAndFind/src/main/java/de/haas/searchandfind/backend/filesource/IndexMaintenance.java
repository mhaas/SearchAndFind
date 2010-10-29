package de.haas.searchandfind.backend.filesource;

import de.haas.searchandfind.backend.App;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.store.Directory;

/**
 *
 * @author laga
 */
public class IndexMaintenance {

    private Directory indexDirectory;
    private static final Logger l = Logger.getLogger(IndexMaintenance.class.getName());

    public IndexMaintenance(Directory indexDir, File targetDir) {
    }

    public void checkForDeletedFiles() throws CorruptIndexException, IOException  {

        boolean readOnly = false;
        IndexSearcher searcher = new IndexSearcher(this.indexDirectory, readOnly);

        int max = searcher.maxDoc();
        for (int ii = 0; ii < max; ii++) {
            Document currentDoc = searcher.doc(ii);
            if (currentDoc == null) {
                continue;
            }
            String pathName = currentDoc.get(App.FIELD_FILE_NAME);
            if (pathName == null) {
                l.severe("No pathname for document " + ii);
            }
            File currentFile = new File(pathName);
            if (!currentFile.exists() || currentFile.isDirectory()) {
                l.info("Deleed document for file " + pathName);
                // TODO: enqueue FileWrapper instance into Queue?
                searcher.getIndexReader().deleteDocument(ii);
            }
        }
        searcher.close();
    }
}
