package org.infretrieval.metrics;

import org.infretrieval.model.SearchResult;

import java.util.List;
import java.util.function.Predicate;

public class Precision {
    public static double calculate(List<SearchResult> results, Predicate<SearchResult> relevanceChecker, int k) {
        var relevantDocs = results.stream().limit(k).filter(relevanceChecker).count();
        return relevantDocs * 1.0 / Math.max(results.size(), k);
    }
}
