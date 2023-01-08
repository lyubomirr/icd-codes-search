package org.infretrieval;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.infretrieval.index.Indexer;
import org.infretrieval.index.Searcher;
import org.infretrieval.reader.DatasetReader;

import java.io.IOException;
import java.util.List;

public class Operations {
    public static void indexTrainSet() throws IOException {
        var reader = new DatasetReader("train.csv");
        try(var indexer = new Indexer("index", new StandardAnalyzer())) {
            indexer.index(reader);
        }
    }

    public static List<String> searchIndex(String query, int n) throws IOException, ParseException {
        var searcher = new Searcher("index", new StandardAnalyzer());
        return searcher.search(query, n);
    }
}
