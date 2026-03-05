package com.example.tpms.service;


import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.InputStream;
import java.util.List;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.ParseContext;


@Service
public class ResumeService {

    private final Tika tika = new Tika();

    private final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String parseText(MultipartFile file) throws Exception {
        // 1. Content Handler: -1 matlab no character limit
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        PDFParserConfig config = new PDFParserConfig();
        config.setSortByPosition(true);
        config.setAverageCharTolerance(1.5f);
        config.setSpacingTolerance(1.0f);
        config.setSuppressDuplicateOverlappingText(true);

        context.set(PDFParserConfig.class, config);
        AutoDetectParser parser = new AutoDetectParser();
        try (InputStream stream = file.getInputStream()) {
            parser.parse(stream, handler, metadata, context);
        }
        String result = handler.toString();
        return cleanUpText(result);
    }

    private String cleanUpText(String text) {
        if (text == null) return "";
        // Multiple empty lines ko single line mein convert karna
        return text.replaceAll("(?m)^[ \t]*\r?\n", "\n").trim();
    }

    public void parseTables(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             PDDocument document = PDDocument.load(is)) {

            ObjectExtractor oe = new ObjectExtractor(document);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator it = oe.extract();
            while (it.hasNext()) {
                Page page = it.next();
                List<Table> tables = sea.extract(page);
                for (Table table : tables) {
                    System.out.println("Table found on page: " + page.getPageNumber());
                }
            }
        }
    }
}
