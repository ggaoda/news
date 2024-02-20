# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html


# useful for handling different item types with a single interface
import logging

from sina.sinaSpider.items import CommentListItem, NewsItem, UserItem
from sina.models.news import News
from sina.models.user import User


logger = logging.getLogger(__name__)


class SaveItems:
    def __init__(self):
        self._newsMap = {}
        self._commentMap = {}

    def process_item(self, item, spider):
        if isinstance(item, CommentListItem):
            newsItem = self._newsMap.get(item['doc_id'])
            commentListItem = item
            if not newsItem:
                logger.info("没有找到评论对应id的新闻，将评论暂时保存")
                self._commentMap[item['doc_id']] = item
                return item
            else:
                logger.info("找到评论对应id的新闻")
                del self._newsMap[item['doc_id']]
        elif isinstance(item, NewsItem):
            # commentListItem = self._commentMap.get(item['doc_id'])
            newsItem = item
            # if not commentListItem:
            #     # 将新闻数据暂时保存到dict中
            #     logger.info("没有找到新闻对应id的评论，将新闻先直接存入数据库")
            #     self._newsMap[item['doc_id']] = item
            #     return item
            # else:
            #     del self._commentMap[item['doc_id']]
        elif isinstance(item, UserItem):
            userModel = User(userItem=item)
            userModel.save()
            logger.info(userModel.nick_name + " 用户数据保存完成！|user_save_suc")
            return item
        else:
            return item

        newsModel = News(newsItem)
        newsModel.save()
        logger.info(newsModel.title + "|新闻保存成功！|news_save_suc")
        return item
