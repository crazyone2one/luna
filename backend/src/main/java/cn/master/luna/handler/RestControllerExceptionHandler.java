package cn.master.luna.handler;

import cn.master.luna.exception.CustomException;
import cn.master.luna.exception.IResultCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Created by 11's papa on 2025/7/2
 */
@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultHandler handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResultHandler.error(CustomHttpResultCode.VALIDATE_FAILED.getCode(),
                CustomHttpResultCode.VALIDATE_FAILED.getMessage(), errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultHandler handleHttpRequestMethodNotSupportedException(HttpServletResponse response, Exception exception) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        return ResultHandler.error(HttpStatus.METHOD_NOT_ALLOWED.value(), exception.getMessage());
    }

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ResultHandler> handleCustomException(CustomException e) {
        IResultCode errorCode = e.getErrorCode();
        if (Objects.isNull(errorCode)) {
            // 如果抛出异常没有设置状态码，则返回错误 message
            return ResponseEntity.internalServerError()
                    .body(ResultHandler.error(CustomHttpResultCode.FAILED.getCode(), e.getMessage()));
        }
        int code = errorCode.getCode();
        String message = errorCode.getMessage();
        if (errorCode instanceof CustomHttpResultCode) {
            // 如果是 MsHttpResultCode，则设置响应的状态码，取状态码的后三位
            return ResponseEntity.status(code % 1000)
                    .body(ResultHandler.error(code, message, e.getMessage()));
        } else {
            // 响应码返回 500，设置业务状态码
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultHandler.error(code, message, e.getMessage()));
        }
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResultHandler> handleException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(ResultHandler.error(CustomHttpResultCode.FAILED.getCode(),
                        e.getMessage(), getStackTraceAsString(e)));
    }

    public static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
