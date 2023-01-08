package org.infretrieval.indexing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private final IndexSearcher searcher;
    private final QueryParser parser;

    public Searcher(String indexPath, Analyzer analyzer) throws IOException {
        var reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        this.searcher = new IndexSearcher(reader);
        this.parser = new QueryParser("description", analyzer);
    }

    public List<String> search(String queryText, int n) throws ParseException, IOException {
        var query = parser.parse(queryText);
        var foundDocs = searcher.search(query, n);

        var results = new ArrayList<String>();
        for(ScoreDoc scoreDoc : foundDocs.scoreDocs) {
            var document = searcher.doc(scoreDoc.doc);
            results.add(document.get("code"));
        }
        return results;
    }
}
