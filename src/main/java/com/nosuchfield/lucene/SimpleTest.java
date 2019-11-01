package com.nosuchfield.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.util.Scanner;

/**
 * 初次了解Lucene
 *
 * @author hourui 2019-10-28 10:27
 */
public class SimpleTest {

    public static void main(String[] args) throws Exception {
//        new SimpleTest().run();
        new SimpleTest().runBoolean();
    }

    /**
     * 执行boolean类型的查询
     */
    private void runBoolean() throws Exception {
        Directory directory = new RAMDirectory();
        System.out.println("数据索引中...");
        index(directory);
        System.out.println("数据索引完毕！");

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        Query q1 = new QueryParser("title", new IKAnalyzer()).parse("编程");
        Query q2 = new QueryParser("title", new IKAnalyzer()).parse("网络");
        builder.add(q1, BooleanClause.Occur.SHOULD);
        builder.add(q2, BooleanClause.Occur.SHOULD);
        // 最少需要两个符合条件的要求，在这里其实就是等价于MUST
        builder.setMinimumNumberShouldMatch(2);


        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(builder.build(), hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        fetchDocs(searcher, hits);
    }

    /**
     * 执行普通的查询
     */
    private void run() throws Exception {
        Directory directory = new RAMDirectory();
        System.out.println("数据索引中...");
        index(directory);
        System.out.println("数据索引完毕！");
        search(directory);
    }

    private void index(Directory directory) throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(directory, config);

        addDoc(indexWriter, "游戏编程算法与技巧", "9787121276453");
        addDoc(indexWriter, "计算机程序的构造和解释(原书第2版) : 原书第2版", "9787111135104");
        addDoc(indexWriter, "编码 : 隐匿在计算机软硬件背后的语言", "9787121106101");
        addDoc(indexWriter, "计算机网络（第5版）", "9787302274629");
        addDoc(indexWriter, "Python网络编程攻略", "9787115372697");
        addDoc(indexWriter, "UNIX网络编程", "9787302119746");
        addDoc(indexWriter, "算法导论（原书第3版）", "9787111407010");

        indexWriter.close();
    }

    private void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        // DocValues用于排序
        doc.add(new SortedDocValuesField("isbn", new BytesRef(isbn)));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }

    private void search(Directory directory) throws Exception {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("Search: ");
            String input = scan.nextLine();
            if ("exit".equals(input)) {
                System.out.println("Bye..");
                System.exit(0);
            }
            search0(directory, input);
        }
    }

    private void search0(Directory directory, String input) throws Exception {
        Query q = new QueryParser("title", new IKAnalyzer()).parse(input);
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        // 使用isbn进行排序
        Sort sort = new Sort(new SortField("isbn", SortField.Type.STRING, false));
        TopDocs docs = searcher.search(q, hitsPerPage, sort);
        ScoreDoc[] hits = docs.scoreDocs;
        fetchDocs(searcher, hits);
    }

    private void fetchDocs(IndexSearcher searcher, ScoreDoc[] hits) throws Exception {
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
        }
        System.out.println();
    }

}
