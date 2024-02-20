# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class NewsItem(scrapy.Item):
    title = scrapy.Field()
    create_time = scrapy.Field()
    channel_name = scrapy.Field()
    doc_id = scrapy.Field()
    url = scrapy.Field()
    html_content = scrapy.Field()
    text_content = scrapy.Field()
    intro = scrapy.Field()
    media_name = scrapy.Field()
    keywords = scrapy.Field()
    comment_total = scrapy.Field()
    view_count = scrapy.Field()
    author = scrapy.Field()


class UserItem(scrapy.Item):
    uid = scrapy.Field()
    nick_name = scrapy.Field()
    introduce = scrapy.Field()
    profile = scrapy.Field()
    gender = scrapy.Field()


class CommentItem(scrapy.Item):
    comment_id = scrapy.Field()
    content = scrapy.Field()
    uid = scrapy.Field()
    doc_id = scrapy.Field()
    time = scrapy.Field()
    agree_nums = scrapy.Field()


class CommentListItem(scrapy.Item):
    comment_list = scrapy.Field()
    doc_id = scrapy.Field()
    view_count = scrapy.Field()
    comment_nums = scrapy.Field()
