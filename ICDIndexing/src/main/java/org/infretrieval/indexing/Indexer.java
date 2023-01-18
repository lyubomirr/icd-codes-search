package org.infretrieval.indexing;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.infretrieval.model.ICDCodeEntry;
import org.infretrieval.reader.DatasetReader;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Paths;

@RequiredArgsConstructor
public class Indexer implements Closeable {
    private final IndexWriter writer;

    public Indexer(String indexPath, Analyzer analyzer) throws IOException {
        var dir = FSDirectory.open(Paths.get(indexPath));
        var iwc = new IndexWriterConfig(analyzer);

        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        writer = new IndexWriter(dir, iwc);
    }

    public void index(DatasetReader reader) throws IOException {
        for(ICDCodeEntry entry : reader) {
            index(entry);
        }
    }

    public void index(ICDCodeEntry entry) throws IOException {
        System.out.println("Indexing document " + entry.index());

        var doc = new Document();

        var id = new StringField("id", Long.toString(entry.index()), Field.Store.YES);
        var code = new StringField("code", entry.code(), Field.Store.YES);
        var shortCode = new StringField("shortCode", entry.shortCode(), Field.Store.YES);
        var description = new TextField("description", entry.description(), Field.Store.NO);

        doc.add(id);
        doc.add(code);
        doc.add(shortCode);
        doc.add(description);

        writer.addDocument(doc);
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
