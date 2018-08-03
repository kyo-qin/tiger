package org.tiger.lucene.common;

public class NewsInfo {
    
    private int id;// 新闻id
    
    private String title;// 新闻标题
    
    private String content;// 新闻内容
    
    private int reply;// 评论数

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

}
