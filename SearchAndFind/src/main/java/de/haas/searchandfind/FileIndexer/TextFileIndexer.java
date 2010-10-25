

package de.haas.searchandfind.FileIndexer;

import java.io.File;
import org.apache.lucene.document.Document;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class TextFileIndexer implements FileIndexer {

    @Override
    public Document makeDocument(File i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
