import sys
from time import sleep
from common_module import init_common, rd
import json
import time
from scrapy.crawler import CrawlerProcess
from scrapy.utils.project import get_project_settings
import threading

LINE_INDEX = lambda name: name + "_" + "line_index"
LOG_FILEPATH = lambda name: name + "_" + "spider.log"
SPIDER_INFO = lambda name: name + "_" + "spider_info"


# 从上一次分析结束的位置开始继续分析日志
# 主要分析已经爬取了多少新闻
def get_logger(spider_name):
    line_index = int(rd.get(LINE_INDEX(spider_name)) or 0)  # 获取开始读取的位置
    print(line_index)
    with open(file=LOG_FILEPATH(spider_name), mode='r', encoding="utf-8") as file:
        lines_tmp = file.readlines()[line_index:] or []
    lines = []
    for line in lines_tmp:
        if len(line.strip()) > 0:
            lines.append(line)
    if len(lines) > 0:
        line_index += len(lines) + 1  # 设置下一次开始读取的位置
        rd.set(LINE_INDEX(spider_name), line_index)
    return lines


# 用于监视爬虫运行状态的
def spider_monitor(spider, spider_name, crawl_num):
    stop_spider = False
    while True:
        # 爬虫运行中
        # 获取日志并进行分析
        logger = get_logger(spider_name)
        spider_info = analyse_log(spider_name=spider_name, logger=logger)
        if spider_info:  # 如果有数据
            if not stop_spider and int(spider_info["saveCount"]) >= crawl_num:
                # 停止爬虫
                spider.stop()
                stop_spider = True
            rd.set(SPIDER_INFO(spider_name), json.dumps(spider_info))  # 设置spider信息
            if spider_info["spiderStatus"] == 2:
                # 退出
                break
        sleep(5)  # 休眠5秒


def analyse_log(spider_name, logger):
    if len(logger) == 0:
        return False
    spider_info = json.loads(rd.get(SPIDER_INFO(spider_name)))  # 从redis中加载爬虫状态数据
    for line in logger:
        res = line.split("|")
        if res[-1].strip() == "news_save_suc":
            # 如果该条记录是保存数据到数据库中
            spider_info["saveCount"] += 1
            spider_info["crawlCount"] += 1
        elif res[-1].strip() == "news_exist":
            # 如果该条记录表示该新闻已存在
            spider_info["repeatCount"] += 1
            spider_info["crawlCount"] += 1
        elif "Dumping Scrapy stats" in res[-1]:
            # 如果该条记录表示爬虫运行结束
            spider_info["spiderStatus"] = 2
            spider_info["endTime"] = int(time.time())
    return spider_info


"""spiderStatus:
                1 spider运行中
                2 spider运行结束"""


def start_spider(spider_name, crawl_num=None):
    # 清空日志数据
    with open(LOG_FILEPATH(spider_name), 'w') as f:
        f.write("")
    process = CrawlerProcess(get_project_settings())
    # 设置爬虫状态信息
    rd.set(LINE_INDEX(spider_name), 0)
    rd.set(SPIDER_INFO(spider_name),
           json.dumps({
               "spiderStatus": 1,
               "startTime": int(time.time()),
               "endTime": -1,
               "repeatCount": 0,
               "crawlCount": 0,
               "saveCount": 0
           }))
    if crawl_num is None:
        crawl_num = sys.maxsize
    # 启动子线程，定时查询爬虫信息
    spider_monitor_thread = threading.Thread(target=spider_monitor, args=(process, spider_name, crawl_num))

    spider_monitor_thread.start()
    # 获取爬虫
    process.crawl(spider_name)
    # 启动
    process.start()


if __name__ == '__main__':
    start_spider(spider_name="sina")
