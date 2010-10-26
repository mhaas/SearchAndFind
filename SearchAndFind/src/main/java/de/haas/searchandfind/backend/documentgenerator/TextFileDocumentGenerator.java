

package de.haas.searchandfind.backend.documentgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;

/**
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class TextFileDocumentGenerator implements FileDocumentGenerator {

    private static class Fieldable {

        public Fieldable() {
        }
    }

    @Override
    public Document makeDocument(File i) {
        
        Document doc = new Document();

        NumericField lastModField = new NumericField("lastModified");
        lastModField.setLongValue(i.lastModified());
        // last-modified date

        
        doc.add(lastModField);
        try {
            // TODO: how is the content indexed now?
            Field contentField = new Field("content", new FileReader(i));
            doc.add(contentField);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFileDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return doc;


    }

}
