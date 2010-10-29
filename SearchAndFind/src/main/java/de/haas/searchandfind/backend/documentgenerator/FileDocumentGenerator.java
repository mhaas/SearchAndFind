package de.haas.searchandfind.backend.documentgenerator;

import de.haas.searchandfind.backend.App;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;

/**
 *
 * @author laga
 */
public abstract class FileDocumentGenerator {

    // TODO: specify schema for Document instances
    /**
     * Creates Document instance from a given File.
     * May return null if File has unsupported type.
     * 
     * Document instance must have at least "fileName" field.
     * 
     * @param i
     * @return
     */
    public abstract Document makeDocument(File i);

    public Field makeKeywordsField(String keyWords) {
        Field keyWordsField = new Field(App.FIELD_KEYWORDS, keyWords, Field.Store.YES, Field.Index.ANALYZED);
        return keyWordsField;
    }

    public Field makeContentField(String content) {
        Field contentField = new Field(App.FIELD_CONTENT, content, Field.Store.NO, Field.Index.ANALYZED);
        return contentField;
    }

    public Field makeFileNameField(String pathName) {
        Field fileNameField = new Field(App.FIELD_FILE_NAME, pathName, Field.Store.YES, Field.Index.NOT_ANALYZED);
        return fileNameField;
    }

    public NumericField makeLastModifiedField(long lastModifiedDate) {
        NumericField lastModField = new NumericField(App.FIELD_LAST_MODIFIED);
        lastModField.setLongValue(lastModifiedDate);
        return lastModField;
    }

    public Field makeContentField(File i) throws FileNotFoundException {
        Field contentField = new Field(App.FIELD_CONTENT, new FileReader(i));
        return contentField;

    }

    public Field makeTitlefield(String title) {
        Field titleField = new Field(App.FIELD_TITLE, title, Field.Store.YES, Field.Index.ANALYZED);
        return titleField;
    }
}
