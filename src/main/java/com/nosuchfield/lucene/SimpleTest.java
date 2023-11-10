package com.nosuchfield.lucene;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.List;

import static com.nosuchfield.lucene.ReadAndWriterCsvFlie.ALL_TITLE_PATH;
import static com.nosuchfield.lucene.ReadAndWriterCsvFlie.QUERY_PATH;
import static com.nosuchfield.lucene.ReadAndWriterCsvFlie.writeCsvFilePath;

/**
 * 初次了解Lucene
 *
 * @author hourui 2019-10-28 10:27
 */
public class SimpleTest {
    List<List<String>> candidates = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        new SimpleTest().run();
//        new SimpleTest().runBoolean();
//        new SimpleTest().runFilter();
    }

    /**
     * 进行filter查询，filter查询不计算score
     */
//    private void runFilter() throws Exception {
//        Directory directory = new RAMDirectory();
//        System.out.println("数据索引中...");
//        index(directory);
//        System.out.println("数据索引完毕！");
//
//        BooleanQuery.Builder builder = new BooleanQuery.Builder();
//        Query q = new TermQuery(new Term("title", "编程"));
//        builder.add(q, BooleanClause.Occur.FILTER);
//
//        int hitsPerPage = 10;
//        IndexReader reader = DirectoryReader.open(directory);
//        IndexSearcher searcher = new IndexSearcher(reader);
//        TopDocs docs = searcher.search(builder.build(), hitsPerPage);
//        ScoreDoc[] hits = docs.scoreDocs;
//        fetchDocs(searcher, hits);
//    }

    /**
     * 执行boolean类型的查询
     */
//    private void runBoolean() throws Exception {
//        Directory directory = new RAMDirectory();
//        System.out.println("数据索引中...");
//        index(directory);
//        System.out.println("数据索引完毕！");
//
//        BooleanQuery.Builder builder = new BooleanQuery.Builder();
////        Query q1 = new QueryParser("title", new Analyzer()).parse("编程");
////        Query q2 = new QueryParser("title", new Analyzer()).parse("网络");
////        builder.add(q1, BooleanClause.Occur.SHOULD);
////        builder.add(q2, BooleanClause.Occur.SHOULD);
//        // 最少需要两个符合条件的要求，在这里其实就是等价于MUST
//        builder.setMinimumNumberShouldMatch(2);
//
//
//        int hitsPerPage = 10;
//        IndexReader reader = DirectoryReader.open(directory);
//        IndexSearcher searcher = new IndexSearcher(reader);
//        TopDocs docs = searcher.search(builder.build(), hitsPerPage);
//        ScoreDoc[] hits = docs.scoreDocs;
//        fetchDocs(searcher, hits);
//    }

    /**
     * 执行普通的查询
     */
    private void run() throws Exception {
        List<String>  q_list = ReadAndWriterCsvFlie.readCsvFile(QUERY_PATH,Boolean.FALSE);
        System.out.println("q_list"+q_list.size());
        List<String>  all_title_list = ReadAndWriterCsvFlie.readCsvFile(ALL_TITLE_PATH,Boolean.TRUE);
        System.out.println("all_title_list"+all_title_list.size());
        Directory directory = new RAMDirectory();
        System.out.println("数据索引中...");
        index(directory,all_title_list);
        System.out.println("数据索引完毕！");
        search(directory,q_list);
        System.out.println("导出CSV文件中...");
        ReadAndWriterCsvFlie.writeCsvFile(writeCsvFilePath,candidates);
        System.out.println("导出成功!");
    }

    private void index(Directory directory,List<String> d_list) throws IOException {
        CharArraySet cas = new CharArraySet( 0, true);
        Iterator<Object> itor = StopAnalyzer.ENGLISH_STOP_WORDS_SET.iterator();
        while (itor.hasNext()) {
            cas.add(itor.next());
        }
        StopAnalyzer analyzer = new StopAnalyzer(cas);
//        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(directory, config);
        for(int row = 0; row < d_list.size(); row++){
//            System.out.println(d_list.get(0));
            addDoc(indexWriter, d_list.get(row));
        }
//        addDoc(indexWriter, "Can't switch branches git");
//        addDoc(indexWriter, "Async wait at function");
//        addDoc(indexWriter, "Async WCF: and wait");
//        addDoc(indexWriter, "Async methods wait");
//        addDoc(indexWriter, "Async wait await");
//        addDoc(indexWriter, "Node.js/Mongoose wait Async For-loop");

        indexWriter.close();
    }

    private void addDoc(IndexWriter w, String title) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        // DocValues用于排序
//        doc.add(new SortedDocValuesField("isbn", new BytesRef(isbn)));
//        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }

    private void search(Directory directory,List<String> q_list) throws Exception {
        for(int row = 0; row < q_list.size(); row++) {
            search0(directory, q_list.get(row));
            System.out.println("row "+row+" finish!");
        }
    }

    private void search0(Directory directory, String input) throws Exception {
        CharArraySet cas = new CharArraySet( 0, true);
        Iterator<Object> itor = StopAnalyzer.ENGLISH_STOP_WORDS_SET.iterator();
        while (itor.hasNext()) {
            cas.add(itor.next());
        }
        Query q = new QueryParser("title", new StopAnalyzer(cas)).parse(QueryParser.escape(input));
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        // 使用isbn进行排序
//        Sort sort = new Sort(new SortField("isbn", SortField.Type.STRING, false));
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        fetchDocs(searcher, hits);
    }

    private void fetchDocs(IndexSearcher searcher, ScoreDoc[] hits) throws Exception {
        List <String> candidate = new ArrayList<>();
//        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
//            float score = hits[i].score;
            Document d = searcher.doc(docId);
            candidate.add(d.get("title"));
//            System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
//            System.out.println((i + 1) + ". " + "\t" + d.get("title")+score);
        }
        candidates.add(candidate);
    }

}
