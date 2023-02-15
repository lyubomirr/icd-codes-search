package org.infretrieval;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.infretrieval.preprocessing.CustomStopwords;

import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
//        var sc = new Scanner(System.in);
//        while (true) {
//           var q = sc.nextLine();
//           var results = Operations.searchIndex(q, 10);
//           System.out.println(String.join("\n", results));
//       }
        //Operations.evaluateOnStemmedIndex(false);
        //Operations.evaluateOnStandardIndex();
        //Operations.indexTrainSet(new BulgarianAnalyzer(), "indices/index-bg-stem");
        //Operations.indexTrainSet(new StandardAnalyzer(), "indices/index");


//        Operations.indexTrainSet(new StandardAnalyzer(new CharArraySet(CustomStopwords.WORDS, true)),
//                "train.csv", "indices/index-custom-stop");
        Operations.evaluateOnCustomStopWordsIndex();
        //Operations.indexTrainSet(new BulgarianAnalyzer(), "train_merged.csv", "indices/index-bg-stem-merged");
        //Operations.evaluateOnStemmedIndexMerged(true);
    }
}