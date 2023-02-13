package org.infretrieval;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.MultiSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.infretrieval.indexing.Indexer;
import org.infretrieval.indexing.Searcher;
import org.infretrieval.metrics.Precision;
import org.infretrieval.metrics.ReciprocalRank;
import org.infretrieval.model.EvaluationResult;
import org.infretrieval.model.ICDCodeEntry;
import org.infretrieval.model.SearchResult;
import org.infretrieval.reader.DatasetReader;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

public class Operations {
    private static final Similarity[] SIMILARITIES = new Similarity[] {
            new BM25Similarity(),
            new ClassicSimilarity(),
            new BooleanSimilarity(),
            new MultiSimilarity(new Similarity[]{ new BM25Similarity(), new ClassicSimilarity()})
    };

    public static void indexTrainSet(Analyzer analyzer, String datasetPath, String indexPath) throws IOException {
        var reader = new DatasetReader(datasetPath);
        try(var indexer = new Indexer(indexPath, analyzer)) {
            indexer.index(reader);
        }
    }

    public static List<SearchResult> searchIndex(String query, int n) throws IOException, ParseException {
        var searcher = new Searcher("indices/index", new StandardAnalyzer(), new BM25Similarity());
        return searcher.search(query, n);
    }

    public static void evaluateOnStemmedIndex() throws IOException, ParseException {
        evaluateTestSet(new BulgarianAnalyzer(), "indices/index-bg-stem");
    }

    public static void evaluateOnStemmedIndexMerged() throws IOException, ParseException {
        evaluateTestSet(new BulgarianAnalyzer(), "indices/index-bg-stem-merged");
    }

    public static void evaluateOnStandardIndex() throws IOException, ParseException {
        evaluateTestSet(new StandardAnalyzer(), "indices/index");
    }

    public static void evaluateOnStandardIndexMerged() throws IOException, ParseException {
        evaluateTestSet(new StandardAnalyzer(), "indices/index-merged");
    }

    private static void evaluateTestSet(Analyzer analyzer, String indexPath)
            throws IOException, ParseException {
        for(var similarity : SIMILARITIES)  {
            System.out.println("Results using short codes:");
            var shortCodesResult = evaluateTestSet(analyzer, indexPath, true, similarity);
            System.out.println(shortCodesResult);

            System.out.println("Results using long codes:");
            var longCodesResult = evaluateTestSet(analyzer, indexPath, false, similarity);
            System.out.println(longCodesResult);
        }
    }

    private static EvaluationResult evaluateTestSet(Analyzer analyzer,
                                                  String indexPath,
                                                  boolean useShortCodes,
                                                  Similarity similarity) throws IOException, ParseException {
        var reader = new DatasetReader("test.csv");
        var searcher = new Searcher(indexPath, analyzer, similarity);

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

        return new EvaluationResult(totalCount,
                precisionAt3Total/totalCount,
                precisionAt5Total/totalCount,
                reciprocalRankTotal/totalCount,
                accurate * 1.0/totalCount,
                similarity.getClass().getSimpleName());
    }
}
