from mongoengine import Document, LongField, ListField, StringField


class UserRecommenderList(Document):
    user_id = LongField(required=True, primary_key=True)
    recommender_list = ListField(StringField())
