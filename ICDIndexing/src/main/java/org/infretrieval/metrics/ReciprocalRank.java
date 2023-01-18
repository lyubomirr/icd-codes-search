package org.infretrieval.metrics;

import org.infretrieval.model.SearchResult;

import java.util.List;
import java.util.function.Predicate;

public class ReciprocalRank {
    public static double calculate(List<SearchResult> results, Predicate<SearchResult> relevanceChecker) {
        for(int i = 0; i < results.size(); i++) {
            if(relevanceChecker.test(results.get(i))) {
                return 1.0 / (i+1);
            }
        }
        return 0;
    }
}
