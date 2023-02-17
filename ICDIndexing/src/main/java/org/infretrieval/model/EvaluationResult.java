package org.infretrieval.model;

public record EvaluationResult(int totalDocs,
                               double precisionAt3,
                               double precisionAt5,
                               double mrr,
                               double accuracy,
                               String similarity) {

    @Override
    public String toString() {
        return "Similarity: " + similarity + "\n" +
                "Total docs: " + totalDocs + "\n" +
                "Precision@3:" + precisionAt3 + "\n" +
                "Precision@5:" + precisionAt5 + "\n" +
                "MRR:" + mrr + "\n" +
                "Accuracy:" + accuracy + "\n";
    }
}
