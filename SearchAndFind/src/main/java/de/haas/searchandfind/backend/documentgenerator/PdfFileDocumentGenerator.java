package de.haas.searchandfind.backend.documentgenerator;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * Creates documents from PDF files.
 *
 * Fills in these fields: fileName, lastMod, content, title, keywords
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
class PdfFileDocumentGenerator extends FileDocumentGenerator {

    private static final Logger l = Logger.getLogger("PdfFileDocumentGenerator");

    @Override
    public Document makeDocument(File i) {

        PDDocument pdf = null;
        Document doc = new Document();
        try {

            pdf = PDDocument.load(i);

            Fieldable lastModField = super.makeLastModifiedField(i.lastModified());
            doc.add(lastModField);
            Field fileNameField = super.makeFileNameField(i.getPath());
            doc.add(fileNameField);

            PDDocumentInformation info = pdf.getDocumentInformation();
            String title =
                    info.getTitle();
            if (title == null) {
                l.info("PDF document does not have title");
            } else {
                l.info("PDF title: " + title);
                Field titleField = super.makeTitlefield(title);
                doc.add(titleField);
            }


            String keyWords = info.getKeywords();
            if (keyWords == null) {
                l.info("PDF document does not have keyWords");
            } else {
                l.info("PDF keywords: " + keyWords);
                Field keyWordsField = super.makeKeywordsField(keyWords);
                doc.add(keyWordsField);
            }
            // TODO: get subject

            // get content
            try {
                PDFTextStripper stripper = new PDFTextStripper();
                String content = stripper.getText(pdf);
                Field contentField = super.makeContentField(content);
                doc.add(contentField);
            } catch (Throwable th) {
                l.severe("Caught Throwable while loading PDF content");
                l.severe(th.toString());
            }
            return doc;


        } catch (IOException ex) {
            Logger.getLogger(PdfFileDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pdf.close();
            } catch (IOException ex) {
                Logger.getLogger(PdfFileDocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }
}
