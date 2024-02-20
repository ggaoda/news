from mongoengine import Document, LongField, StringField, ListField, FloatField


class NewsEmbedding(Document):
    doc_id = StringField(required=True, primary_key=True)
    embedding = ListField(FloatField())
    create_time = LongField()
