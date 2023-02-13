package org.infretrieval.indexing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.infretrieval.model.SearchResult;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private final IndexSearcher searcher;
    private final QueryParser parser;

    public Searcher(String indexPath, Analyzer analyzer, Similarity similarity) throws IOException {
        var reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        this.searcher = new IndexSearcher(reader);
        this.searcher.setSimilarity(similarity);
        this.parser = new QueryParser("description", analyzer);
    }

    public List<SearchResult> search(String queryText, int n) throws ParseException, IOException {
        var query = parser.parse(QueryParser.escape(queryText));
        var foundDocs = searcher.search(query, n);

        var results = new ArrayList<SearchResult>();
        for(ScoreDoc scoreDoc : foundDocs.scoreDocs) {
            var document = searcher.doc(scoreDoc.doc);
            results.add(new SearchResult(document.get("code"), document.get("shortCode")));
        }
        return results;
    }
}
