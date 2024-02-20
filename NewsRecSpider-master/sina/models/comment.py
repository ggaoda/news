from mongoengine import Document, StringField, LongField, IntField

from sina.sinaSpider.items import CommentItem


class Comment(Document):
    comment_id = StringField(required=True, primary_key=True)
    content = StringField(required=True)
    uid = LongField(required=True)
    doc_id = StringField(required=True)
    time = LongField(required=True)
    agree_nums = IntField(default=0)

    def __init__(self, commentItem: CommentItem = None, *args, **values):
        super().__init__(*args, **values)
        if commentItem:
            attrList = ['comment_id', 'content', 'uid', 'doc_id', 'time', 'agree_nums']
            for attr in attrList:
                self[attr] = commentItem.get(attr, None)

    def toStr(self):
        return "===============\n" \
               "评论内容：{}\n" \
               "评论时间：{}\n" \
               "评论点赞数：{}\n" \
               "===============\n".format(self.content, self.time, self.agree_nums)
