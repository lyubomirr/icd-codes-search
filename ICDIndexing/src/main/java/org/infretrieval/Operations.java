package org.infretrieval;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.infretrieval.indexing.Indexer;
import org.infretrieval.indexing.Searcher;
import org.infretrieval.metrics.Precision;
import org.infretrieval.metrics.ReciprocalRank;
import org.infretrieval.model.ICDCodeEntry;
import org.infretrieval.model.SearchResult;
import org.infretrieval.reader.DatasetReader;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public class Operations {
    public static void indexTrainSet(Analyzer analyzer, String indexPath) throws IOException {
        var reader = new DatasetReader("train.csv");
        try(var indexer = new Indexer(indexPath, analyzer)) {
            indexer.index(reader);
        }
    }

    public static List<SearchResult> searchIndex(String query, int n) throws IOException, ParseException {
        var searcher = new Searcher("indices/index", new StandardAnalyzer());
        return searcher.search(query, n);
    }

    public static void evaluateOnStemmedIndex(boolean useShortCodes) throws IOException, ParseException {
        evaluateTestSet(new BulgarianAnalyzer(), "indices/index-bg-stem", useShortCodes);
    }

    public static void evaluateOnStandardIndex(boolean useShortCodes) throws IOException, ParseException {
        evaluateTestSet(new StandardAnalyzer(), "index", useShortCodes);
    }

    private static void evaluateTestSet(Analyzer analyzer, String indexPath, boolean useShortCodes)
            throws IOException, ParseException {
        var reader = new DatasetReader("test.csv");
        var searcher = new Searcher(indexPath, analyzer);

        double precisionAt5Total = 0.0;
        double precisionAt3Total = 0.0;
        double reciprocalRankTotal = 0.0;
        int accurate = 0;
        int totalCount = 0;

        for(ICDCodeEntry entry : reader) {
            var results = searcher.search(entry.description(), 10);

            Predicate<SearchResult> checker = useShortCodes
                    ? result -> result.shortCode().equals(entry.shortCode())
                    : result -> result.code().equals(entry.code());

            precisionAt5Total += Precision.calculate(results, checker, 5);
            precisionAt3Total += Precision.calculate(results, checker, 3);
            reciprocalRankTotal += ReciprocalRank.calculate(results, checker);
            accurate = !results.isEmpty() && checker.test(results.get(0)) ? accurate + 1 : accurate;

            totalCount++;
        }

        System.out.println("Total docs: " + totalCount);
        System.out.printf("Precision@5: %f\n", precisionAt5Total/totalCount);
        System.out.printf("Precision@3: %f\n", precisionAt3Total/totalCount);
        System.out.printf("MRR: %f\n", reciprocalRankTotal/totalCount);
        System.out.printf("Accuracy: %f\n", accurate * 1.0/totalCount);
    }
}
