

package de.haas.searchandfind.DocumentGenerator;

import java.io.File;
import org.apache.lucene.document.Document;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class TextFileDocumentGenerator implements FileDocumentGenerator {

    @Override
    public Document makeDocument(File i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
