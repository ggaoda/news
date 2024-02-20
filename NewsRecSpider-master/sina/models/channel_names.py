from mongoengine import Document, StringField, IntField, ObjectIdField


class ChannelName(Document):
    meta = {
        "db_alias": "target",
        "collection": "news_channel"
    }
    _id = ObjectIdField()
    channel_name = StringField()
    count = IntField()
    _class = StringField(default="")
