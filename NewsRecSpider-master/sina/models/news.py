from mongoengine import Document, StringField, IntField, LongField, BooleanField

from sina.sinaSpider.items import NewsItem


class News(Document):
    meta = {
        "db_alias": "source"
    }
    title = StringField(required=True)
    create_time = LongField(required=True)
    channel_name = StringField(default="")
    doc_id = StringField(required=True, primary_key=True)
    url = StringField(required=True)
    html_content = StringField(required=True)
    text_content = StringField(required=True)
    intro = StringField(default="")
    media_name = StringField(default="")
    keywords = StringField(default="")
    comment_total = IntField(default=0)
    view_count = IntField(default=0)
    author = StringField(default="新浪新闻")
    _class = StringField(default="")

    def __init__(self, newsItem: NewsItem = None, *args, **values):
        super().__init__(*args, **values)
        if newsItem:
            attrList = ['title', 'create_time', 'channel_name', 'author', 'doc_id', 'url', 'html_content',
                        'text_content',
                        'intro', 'view_count', 'comment_total', 'keywords', 'media_name']
            for attr in attrList:
                self[attr] = newsItem.get(attr, None)

    def toStr(self):
        return "===========================\n" \
               "标题：{}\n" \
               "发布时间：{}\n" \
               "频道：{}\n" \
               "文章id：{}\n" \
               "文章地址：{}\n" \
               "Text文本：{}\n" \
               "摘要：{}\n" \
               "关键字：[{}]\n" \
               "作者：{}\n" \
               "评论数：{}\n" \
               "===========================".format(self.title, self.create_time, self.channel_name, self.doc_id,
                                                    self.url, self.text_content, self.intro, self.keywords, self.author,
                                                    self.comment_total)


if __name__ == '__main__':
    newsItem = NewsItem()
    newsItem['title'] = "23"
    newsItem['channel_name'] = "132"
    newsItem['author'] = "132"
    newsItem['doc_id'] = "132"
    newsItem['media_name'] = "132"
    newsItem['comment_id'] = "132"
    newsItem['keywords'] = "132"
    print(News(newsItem).toStr())
