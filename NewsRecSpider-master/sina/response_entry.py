def success(data=None):
    return {
        "code": 200,
        "message": "成功！",
        "data": data
    }


def error(code, message, data=None):
    return {
        "code": code,
        "message": message,
        "data": data
    }
