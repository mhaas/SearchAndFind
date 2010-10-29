

package de.haas.searchandfind.backend.documentgenerator;

import de.haas.searchandfind.backend.App;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    @Override
    public Document makeDocument(File i) {
        
        Document doc = new Document();
        try {
            Field fileNameField = new Field(App.FIELD_FILE_NAME, i.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED);
            doc.add(fileNameField);
        } catch (IOException ex) {
            Logger.getLogger(TextFileDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        NumericField lastModField = new NumericField(App.FIELD_LAST_MODIFIED);
        lastModField.setLongValue(i.lastModified());
        // last-modified date
        doc.add(lastModField);

        try {
            // TODO: how is the content indexed now?
            Field contentField = new Field(App.FIELD_CONTENT, new FileReader(i));
            doc.add(contentField);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFileDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return doc;


    }

}
