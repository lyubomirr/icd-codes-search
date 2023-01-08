package org.infretrieval;

import org.apache.lucene.queryparser.classic.ParseException;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        var results = Operations.searchIndex("Възпалителни увреждания на семенната връв, на влагалищната обвивка и на" +
                " семенния канал", 10);
        System.out.println(String.join("\n", results));
    }
}