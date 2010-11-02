package de.haas.searchandfind.frontend;

import de.haas.searchandfind.common.Constants;
import java.awt.Component;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class QueryRunner extends Thread {

    private Query query;
    private IndexSearcher searcher;
    private static final int MAX_QUERY_RESULTS = 100;
    private JFrame frame;
    private JList list;
    private static final Logger l = Logger.getLogger("QueryRunner");

    public QueryRunner(Query userQuery, IndexSearcher indexSearcher, JFrame mainFrame, JList resultList) {
        this.query = userQuery;
        this.searcher = indexSearcher;
        this.frame = mainFrame;
        this.list = resultList;
        l.setLevel(Level.FINEST);
    }

    @Override
    public void run() {
        this.doQuery();

    }

    private void doQuery() {
        // signal activity to user
        //      frame.setEnabled(false);
        // TODO: ugly!
        DefaultListModel model = (DefaultListModel) this.list.getModel();
        l.fine("Clearing list model");
        model.clear();

        try {
            l.info("Running Query " + this.query.toString());
            TopDocs results = searcher.search(this.query, MAX_QUERY_RESULTS);
            ScoreDoc[] docs = results.scoreDocs;
            l.info("Got this many results: " + docs.length);
            for (int ii = 0; ii < docs.length; ii++) {
                ScoreDoc currentScoreDocument = docs[ii];
                int docIndex = currentScoreDocument.doc;
                Document currentDocument = searcher.doc(docIndex);
                String pathName = currentDocument.get(Constants.FIELD_FILE_NAME);
                l.info("pathName for current result is: " + pathName);
                QueryResult res = new QueryResult(pathName, currentScoreDocument.score);
                model.addElement(res);
            }
        } catch (IOException ex) {
            Logger.getLogger(QueryRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
