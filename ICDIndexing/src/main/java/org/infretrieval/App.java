package org.infretrieval;

import org.apache.lucene.queryparser.classic.ParseException;
import org.infretrieval.model.SearchResult;

import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        var sc = new Scanner(System.in);
        while (true) {
           var q = sc.nextLine();
           var results = Operations.searchIndex(q, 10);
           System.out.println(results.stream()
                   .map(SearchResult::code)
                   .collect(Collectors.joining("\n")));
       }
    }
}