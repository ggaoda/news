import json
import logging
from datetime import datetime

import scrapy
from mongoengine import connect, Q
from scrapy import Request, Selector
from mongoengine.context_managers import switch_db

from sina.sinaSpider.items import NewsItem, CommentItem, CommentListItem, UserItem
from sina.models.news import News
from sina.models.user import User

logger = logging.getLogger(__name__)


class SinaSpider(scrapy.Spider):
    name = 'sina'
    _headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                      "Chrome/88.0.4324.146 Safari/537.36 ",
    }
    custom_settings = {
        "LOG_FILE": "sina_spider.log"
    }

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.COMMENT_API_TEMPLATE = "https://comment.sina.com.cn/page/info?version=1&format=json&page_size=5&channel={}&newsid={}"
        self.USER_API = "https://weibo.cn/{}"
        connect("news_db", alias="source")
        connect("news_recommender", alias="target")

    def generateUrl(self, urlTemplate: str, argsDict: dict) -> str:
        for key, value in argsDict.items():
            urlTemplate += ("&" + key + "=" + str(value))
        return urlTemplate

    def start_requests(self):
        CHANNEL_LIST_API = "https://feed.mix.sina.com.cn/api/roll/get?pageid=153&num=30"
        channelList = [str(i) for i in range(2510, 2519)]
        channelList.append("2669")

        for lid in channelList:
            for pageNum in range(1, 5):
                url = self.generateUrl(CHANNEL_LIST_API, {"lid": lid, "page": pageNum})
                yield Request(url)

    def parse(self, response):
        # 处理channel数据
        content = response.body.decode("utf-8")
        responseData = json.loads(content)
        dataList = responseData['result']['data']
        for data in dataList:
            news = NewsItem()
            docId = data['docid'].replace(":", "-")
            queryList = News.objects(Q(doc_id=docId))
            with switch_db(News, "target"):
                queryList2 = News.objects(Q(doc_id=docId))
            if queryList or queryList2:
                logger.info("docId: {} 已经存在了|news_exist".format(docId))
                logger.info(data['title'])
                continue
            news['doc_id'] = docId
            news['url'] = data['url']
            news['title'] = data['title']
            news['intro'] = data['intro']
            news['media_name'] = data['media_name']
            news['keywords'] = data['keywords']
            news['create_time'] = int(data['ctime'])
            yield Request(url=news['url'], meta={'item': news}, callback=self.parseNews, headers=self._headers)

    def parseComments(self, response):
        content = response.body.decode("utf-8")
        commentJson = json.loads(content)
        cmtList = commentJson['result']['cmntlist']
        if not cmtList:
            cmtList = []
        docId = commentJson['result']['news']['newsid']
        commentList = []
        commentListItem = CommentListItem()
        commentListItem['comment_nums'] = commentJson['result']['count']['show']
        commentListItem['view_count'] = commentJson['result']['count']['total']
        commentListItem['doc_id'] = docId
        for comment in cmtList:
            commentItem = CommentItem()
            commentItem['comment_id'] = comment['mid']
            commentItem['uid'] = int(comment['uid'])
            commentItem['content'] = comment['content']
            commentItem['agree_nums'] = int(comment['agree'])
            commentItem['time'] = int(datetime.strptime(comment['time'], "%Y-%m-%d %H:%M:%S").timestamp())
            commentItem['doc_id'] = docId
            commentList.append(commentItem)
            uid = int(comment['uid'])
            queryList = User.objects(Q(uid=uid))
            if queryList:
                logger.info("uid: {}|已经存在了|user_exist".format(uid))
                continue
            yield Request(url=self.USER_API.format(uid), callback=self.parseUser, headers={
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                              "Chrome/88.0.4324.146 Safari/537.36 ",
                'cookie': "_T_WM=406311e71bcb886845b03ad7b58fd3f4; SCF=Aks3Iw2sKJbHHy45RrKt1wgLnC0FsYniBX5gXhRHkiTNNlySg3sG_N4CBkdekbyAv3RZ-f4-ysa6wYEYBEXqmSg.; SUB=_2A25NO2HKDeRhGeNK7lUW9yzOyDSIHXVuxA-CrDV6PUJbktANLRDEkW1NSOs1eEXgOGn_z6KUDCL195fOSCKDnHbo; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9W5cZakBnpLp6YX_n.g_Tv655NHD95QfSh-NS0MEeoeRWs4DqcjMi--NiK.Xi-2Ri--ciKnRi-zNSKBfS0MNeoz015tt; SSOLoginState=1614746010"})

        commentListItem['comment_list'] = commentList
        yield commentListItem

    def parseUser(self, response):
        content = response.body.decode("utf-8")
        logger.info("解析用户数据")
        if "如果没有自动跳转" in content:
            return
        selector = Selector(text=content)
        profileUrl = "".join(selector.xpath("//img[@class='por']/@src").extract())
        tmp = "".join(
            selector.xpath("//div[@class='ut']//span[@class='ctt'][1]/text()").extract()).strip().split(
            "\xa0")
        nickName = tmp[0]
        gender = tmp[1].split("/")[0]
        introduce = "".join(
            selector.xpath("//div[@class='ut']//span[@class='ctt'][2]/text()").extract()).strip()
        uid = response.request.url.split("/")[-1]
        print("用户ID：" + uid)
        if "?" in uid:
            uid = int(uid.split("?")[0])
        if gender == "女":
            gender = 1
        else:
            gender = 0
        logger.info(nickName + " 用户数据解析成功")
        userItem = UserItem()
        userItem['uid'] = uid
        userItem['profile'] = profileUrl
        userItem['nick_name'] = nickName
        userItem['gender'] = gender
        userItem['introduce'] = introduce
        yield userItem

    def getHtmlAndText(self, selector):
        logger.info("开始提取文章内容。。。")
        articleBody = selector.xpath("//div[@id='article']")
        if not articleBody:
            articleBody = selector.xpath("//div[@id='artibody']")
        htmlContent = "".join(articleBody.extract()).strip()
        logger.info("提取HTML文本内容成功。。。")
        pList = articleBody.xpath(".//p/text()|.//p//font/text()").extract()

        def deleteEmpty(val):
            val = val.strip()
            if val:
                return True
            else:
                return False

        pList = filter(deleteEmpty, pList)
        textContent = ""
        for p in pList:
            textContent += "".join(p).strip() + " "
        logger.info("提取TEXT文本内容成功。。。")
        return {"htmlContent": htmlContent, "textContent": textContent}

    def parseNews(self, response):
        newsItem = response.meta['item']
        content = response.body.decode("utf-8")
        sel = Selector(text=content)
        logger.info("处理的文章标题为：" + newsItem['title'])
        # 调用函数来解析HTML网页数据
        contentDict = self.getHtmlAndText(selector=sel)
        newsItem['html_content'] = contentDict["htmlContent"]
        newsItem['text_content'] = contentDict["textContent"]
        # 提取新闻频道数据
        newsItem['channel_name'] = "".join(sel.xpath("//div[@class='channel-path']/a[1]/text()").extract()).strip()
        # 提取关键词数据
        keywords = "".join(sel.xpath("//div[@id='keywords']/@data-wbkey").extract()).strip()
        if keywords:
            # 对关键词拆分
            keywords = keywords.split(",")[:-1]
            newsItem['keywords'] = "".join(map(lambda x: x + " ", keywords))
        # 提取作者数据
        author = sel.xpath("//span[@data-sudaclick='content_author_p']/text()").extract()
        if author:
            newsItem['author'] = "".join(author).strip()[3:]
        yield newsItem
