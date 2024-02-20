from mongoengine import Document, StringField, LongField, IntField

from sina.sinaSpider.items import UserItem


class User(Document):
    uid = LongField(required=True, primary_key=True)
    nick_name = StringField(required=True)
    introduce = StringField(default="这个人还没有简介。。。")
    profile = StringField(required=True)
    gender = IntField(default=0)

    def __init__(self, userItem: UserItem = None, *args, **values):
        super().__init__(*args, **values)
        if userItem:
            attrList = ['uid', 'nick_name', 'introduce', 'profile', 'gender']
            for attr in attrList:
                self[attr] = userItem.get(attr, None)
