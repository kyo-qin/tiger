package org.tiger.lucene.common;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);

    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testIKAnalyser() {
        String news = "因本人能力有限,所以也只是放出我在更新版本的时候,项目中用到的需要升级的地方,其他还未研究. ";
        IKAnalyzer analyzer = new IKAnalyzer(true);
        TokenStream ts = analyzer.tokenStream("", news);
        CharTermAttribute attribute = ts.getAttribute(CharTermAttribute.class);
        try {
            ts.reset();
            while (ts.incrementToken()) {
                logger.info(attribute.toString() + "|");
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            analyzer.close();
        }
        assertTrue(true);
    }

    public void testCreateIndex() {
        NewsInfo news1 = new NewsInfo();
        news1.setId(1);
        news1.setTitle("安倍晋三本周会晤特朗普 将强调日本对美国益处");
        news1.setContent("日本首相安倍晋三计划2月10日在华盛顿与美国总统特朗普举行会晤时提出加大日本在美国投资的设想");
        news1.setReply(672);

        NewsInfo news2 = new NewsInfo();
        news2.setId(2);
        news2.setTitle("北大迎4380名新生 农村学生700多人近年最多");
        news2.setContent("昨天，北京大学迎来4380名来自全国各地及数十个国家的本科新生。其中，农村学生共700余名，为近年最多...");
        news2.setReply(995);

        NewsInfo news3 = new NewsInfo();
        news3.setId(3);
        news3.setTitle("特朗普宣誓(Donald Trump)就任美国第45任总统");
        news3.setContent("当地时间1月20日，唐纳德·特朗普在美国国会宣誓就职，正式成为美国第45任总统。");
        news3.setReply(1872);

        Date start = new Date();
        logger.info("**********开始创建索引**********");

        // 创建IK分词器
        Analyzer analyzer = new IKAnalyzer();// 使用IK最细粒度分词
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        // CREATE 表示先清空索引再重新创建
        indexWriterConfig.setOpenMode(OpenMode.CREATE);
        Directory dir = null;
        IndexWriter inWriter = null;
        String indexPathStr = "D:/develop/lucene/data";

        // 存储索引的目录
        Path indexPath = Paths.get(indexPathStr);

        try {
            if (!Files.isReadable(indexPath)) {
                System.out.println("索引目录 '" + indexPath.toAbsolutePath() + "' 不存在或者不可读,请检查");
                assertTrue(false);
            }
            dir = FSDirectory.open(indexPath);
            inWriter = new IndexWriter(dir, indexWriterConfig);
            // 设置新闻ID索引并存储
            FieldType idType = new FieldType();
            idType.setIndexOptions(IndexOptions.DOCS);
            idType.setStored(true);

            // 设置新闻标题索引文档、词项频率、位移信息和偏移量，存储并词条化
            FieldType titleType = new FieldType();
            titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            titleType.setStored(true);
            titleType.setTokenized(true);

            FieldType contentType = new FieldType();
            contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            contentType.setStored(true);
            contentType.setTokenized(true);
            contentType.setStoreTermVectors(true);
            contentType.setStoreTermVectorPositions(true);
            contentType.setStoreTermVectorOffsets(true);

            Document doc1 = new Document();
            doc1.add(new Field("id", String.valueOf(news1.getId()), idType));
            doc1.add(new Field("title", news1.getTitle(), titleType));
            doc1.add(new Field("content", news1.getContent(), contentType));
            doc1.add(new IntPoint("reply", news1.getReply()));
            doc1.add(new StoredField("reply_display", news1.getReply()));

            Document doc2 = new Document();
            doc2.add(new Field("id", String.valueOf(news2.getId()), idType));
            doc2.add(new Field("title", news2.getTitle(), titleType));
            doc2.add(new Field("content", news2.getContent(), contentType));
            doc2.add(new IntPoint("reply", news2.getReply()));
            doc2.add(new StoredField("reply_display", news2.getReply()));

            Document doc3 = new Document();
            doc3.add(new Field("id", String.valueOf(news3.getId()), idType));
            doc3.add(new Field("title", news3.getTitle(), titleType));
            doc3.add(new Field("content", news3.getContent(), contentType));
            doc3.add(new IntPoint("reply", news3.getReply()));
            doc3.add(new StoredField("reply_display", news3.getReply()));

            inWriter.addDocument(doc1);
            inWriter.addDocument(doc2);
            inWriter.addDocument(doc3);
            inWriter.commit();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            try {
                inWriter.close();
                dir.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        Date end = new Date();
        logger.info("索引文档用时:" + (end.getTime() - start.getTime()) + " milliseconds");
        logger.info("**********索引创建完成**********");
    }

    public void testSearchDoc() {
        // 搜索单个字段
        String field = "title";
        // 搜索多个字段时使用数组
        // String[] fields = { "title", "content" };
        String indexPathStr = "D:/develop/lucene/data";
        Path indexPath = Paths.get(indexPathStr);
        Directory dir = null;
        IndexReader reader = null;
        try {
            dir = FSDirectory.open(indexPath);
            reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new IKAnalyzer(false);// 最细粒度分词
            QueryParser parser = new QueryParser(field, analyzer);

            // 多域搜索
            // MultiFieldQueryParser multiParser = new
            // MultiFieldQueryParser(fields, analyzer);

            // 关键字同时成立使用 AND, 默认是 OR
            parser.setDefaultOperator(Operator.AND);

            // 查询语句
            Query query = parser.parse("农村学生");// 查询关键词
            System.out.println("Query:" + query.toString());

            // 返回前10条
            TopDocs tds = searcher.search(query, 10);
            for (ScoreDoc sd : tds.scoreDocs) {
                // Explanation explanation = searcher.explain(query, sd.doc);
                // System.out.println("explain:" + explanation + "\n");
                Document doc = searcher.doc(sd.doc);
                logger.info("DocID:" + sd.doc);
                logger.info("id:" + doc.get("id"));
                logger.info("title:" + doc.get("title"));
                logger.info("content:" + doc.get("content"));
                logger.info("文档评分:" + sd.score);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            try {
                dir.close();
                reader.close();
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public void testHighlight() {
        String field = "title";
        String indexPathStr = "D:/develop/lucene/data";
        Path indexPath = Paths.get(indexPathStr);
        Directory dir = null;
        IndexReader reader = null;
        try {
            dir = FSDirectory.open(indexPath);
            reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new IKAnalyzer();
            QueryParser parser = new QueryParser(field, analyzer);
            Query query = parser.parse("北大学生");
            logger.info("Query:" + query);

            // 查询高亮
            QueryScorer score = new QueryScorer(query, field);
            SimpleHTMLFormatter fors = new SimpleHTMLFormatter("<span style=\"color:red;\">", "</span>");// 定制高亮标签
            Highlighter highlighter = new Highlighter(fors, score);// 高亮分析器

            // 返回前10条
            TopDocs tds = searcher.search(query, 10);
            for (ScoreDoc sd : tds.scoreDocs) {
                // Explanation explanation = searcher.explain(query, sd.doc);
                // System.out.println("explain:" + explanation + "\n");
                Document doc = searcher.doc(sd.doc);
                System.out.println("id:" + doc.get("id"));
                System.out.println("title:" + doc.get("title"));
                Fragmenter fragment = new SimpleSpanFragmenter(score);
                highlighter.setTextFragmenter(fragment);
                // TokenStream tokenStream =
                // TokenSources.getAnyTokenStream(searcher.getIndexReader(),
                // sd.doc, field, analyzer);// 获取tokenstream
                // String str = highlighter.getBestFragment(tokenStream,
                // doc.get(field));// 获取高亮的片段

                String str = highlighter.getBestFragment(analyzer, field, doc.get(field));// 获取高亮的片段
                logger.info("高亮的片段:" + str);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }

    }
}
