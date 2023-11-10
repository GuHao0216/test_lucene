package com.nosuchfield.lucene;
import java.util.Iterator;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
//import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.util.Scanner;

/**
 * 初次了解Lucene
 *
 * @author hourui 2019-10-28 10:27
 */
public class stopAnalyzerTest {

    public static void main(String[] args) throws Exception {
//        new SimpleTest().run();
//        new SimpleTest().runBoolean();
//        new SimpleTest().runFilter();
        try {
            // 要处理的文本
            // "lucene分析器使用分词器和过滤器构成一个“管道”，文本在流经这个管道后成为可以进入索引的最小单位，因此，一个标准的分析器有两个部分组成，一个是分词器tokenizer,它用于将文本按照规则切分为一个个可以进入索引的最小单位。另外一个是TokenFilter，它主要作用是对切出来的词进行进一步的处理（如去掉敏感词、英文大小写转换、单复数处理）等。lucene中的Tokenstram方法首先创建一个tokenizer对象处理Reader对象中的流式文本，然后利用TokenFilter对输出流进行过滤处理";
            String text = "The Lucene PMC is pleased to announce the release of the Apache Solr Reference Guide for Solr 4.4.";

            // 自定义停用词
//            String[] self_stop_words = { "分析", "release", "Apache" };
            CharArraySet cas = new CharArraySet( 0, true);
//            for (int i = 0; i < self_stop_words.length; i++) {
//                cas.add(self_stop_words[i]);
//            }

            // 加入系统默认停用词
            Iterator<Object> itor = StopAnalyzer.ENGLISH_STOP_WORDS_SET.iterator();
            while (itor.hasNext()) {
                cas.add(itor.next());
            }

            // 停用词分词器(去除一些常有a,the,an等等，也可以自定义禁用词)
            StopAnalyzer sa = new StopAnalyzer(cas);
//            StopFilter sa = new StopFilter(in,cas);

            TokenStream ts = sa.tokenStream("field", text);
            CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);

            ts.reset();
            while (ts.incrementToken()) {
                System.out.println(ch);
            }
            ts.end();
            ts.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}