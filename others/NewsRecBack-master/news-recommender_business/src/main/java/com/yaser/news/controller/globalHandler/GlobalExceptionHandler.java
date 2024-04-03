package com.yaser.news.controller.globalHandler;

import com.yaser.news.constant.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.OK)
    public HttpResult<Object> OtherExceptionHandler(Exception e) {

        log.error("服务器运行出错了：" + e.getMessage());
        this.printErrorStack(e);
        return new HttpResult<>(ResultCode.ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(APIException.class)
//    @ResponseStatus(HttpStatus.OK)
    public HttpResult<Object> APIExceptionHandler(APIException e) {
        log.error("API出错了：" + e.getMessage());
        this.printErrorStack(e);

        return new HttpResult<>(e.getCode(), e.getMessage());
    }

    /**
     * 方法参数错误异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.OK)
    public HttpResult<Object> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<String> list = new ArrayList<>();
        // 从异常对象中拿到ObjectError对象
        if (!e.getBindingResult().getAllErrors().isEmpty()) {
            for (ObjectError error : e.getBindingResult().getAllErrors()) {
                list.add(error.getDefaultMessage());
            }
        }
        log.error("方法参数错误异常：" + list.toString());
        this.printErrorStack(e);

        // 然后提取错误提示信息进行返回
        return new HttpResult<>(ResultCode.VALIDATE_FAILED, list);
    }

    private void printErrorStack(Exception e) {
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        String errorMessage =
                "\n\t错误文件：" + stackTraceElement.getFileName() +
                        "\n\t错误类：" + stackTraceElement.getClassName() +
                        "\n\t错误方法：" + stackTraceElement.getMethodName() +
                        "\n\t错误行：" + stackTraceElement.getLineNumber();
        log.error(errorMessage);
    }
}
