package com.nosuchfield.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * Near Real Time 查询测试
 *
 * @author hourui 2019-10-28 11:21
 */
public class NRTTest {

    public static void main(String[] args) throws Exception {
        new NRTTest().run();
    }

    private void run() throws Exception {
        Analyzer analyzer = new IKAnalyzer();
        RAMDirectory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        final IndexWriter indexWriter = new IndexWriter(index, config);

        // NRT查询的相关配置
        TrackingIndexWriter trackingIndexWriter = new TrackingIndexWriter(indexWriter);
        final ReferenceManager<IndexSearcher> searcherManager =
                new SearcherManager(indexWriter, true, null);

        // NRT查询的定时刷新线程
        ControlledRealTimeReopenThread<IndexSearcher> nrtReopenThread = new ControlledRealTimeReopenThread<>(
                trackingIndexWriter, searcherManager, 1.0, 0.1);
        nrtReopenThread.setName("NRT Reopen Thread");
        nrtReopenThread.setPriority(Math.min(Thread.currentThread().getPriority() + 2, Thread.MAX_PRIORITY));
        nrtReopenThread.setDaemon(true);
        nrtReopenThread.start();

        // 查询指定的数据
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    IndexSearcher searcher = searcherManager.acquire();
                    Query q = new TermQuery(new Term("name", "南京"));
                    TopDocs docs = searcher.search(q, 10);
                    System.out.println("Found " + docs.totalHits + " docs");
                    if (docs.totalHits > 0) {
                        int docId = docs.scoreDocs[0].doc;
                        Document d = searcher.doc(docId);
                        System.out.println(d.get("name") + "\t" + d.get("time"));
                    }
                    searcherManager.release(searcher);

                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(1000 * 5);

        System.out.println("写入数据...");
        Document doc = new Document();
        doc.add(new LongField("time", System.currentTimeMillis(), Field.Store.YES));
        doc.add(new StringField("name", "南京", Field.Store.YES));
        indexWriter.addDocument(doc);
        searcherManager.maybeRefresh();

        Thread.sleep(1000 * 2);
    }

}
