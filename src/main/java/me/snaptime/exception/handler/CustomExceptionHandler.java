package me.snaptime.exception.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.exception.CustomException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{

    //RequestBody의 @Valid 유효성검사 실패 시
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.error(errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CommonResponseDto("올바르지 않은 입력값입니다",errors));
    }

    // 역직렬화 과정에서 dto필드의 타입이 맞지 않아 발생하는 예외
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String errorMessage = ex.getMessage();
        if(errorMessage.contains("java.lang.Long"))
            errorMessage = ": Long타입 예외";
        else if(errorMessage.contains("Enum"))
            errorMessage = ": ENUM타입 예외";
        else
            errorMessage = ": 기타 타입예외";

        String message = "올바른 요청타입이 아닙니다.";
        log.error(message+errorMessage+" - "+ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CommonResponseDto(message+errorMessage,null));
    }

    /*
        requestParam값이 존재하지 않는 경우. 공백이나 빈칸이 아닌 NULL일때 발생하는 예외처리로직.
        ENUM타입의 값이 공백이거나 빈칸이여도 이 예외가 발생한다.
    */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        String message = "올바르지 않은 입력값입니다. "+ex.getParameterName()+"을 입력해주세요.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponseDto.of(message,null));
    }

    // custom예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponseDto> handleCustomException(CustomException ex){
        log.error("예외가 발생했습니다.:"+ex.getExceptionCode().getMessage());

        return ResponseEntity.status(ex.getExceptionCode().getStatus())
                .body(new CommonResponseDto(ex.getExceptionCode().getMessage(),null));
    }

    // requestParam으로 입력받은 값의 유효성검사 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponseDto> handleContranintViolation(ConstraintViolationException ex){
        String message = ex.getMessage();

        // DTO명을 노출하지 않기 위한 DTO이름 제거
        int index = message.indexOf(":");
        if (index >= 0) {
            message = message.substring(index + 1).trim();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponseDto(message,null));
    }

    // @PathVariable나 @RequestParam으로 입력받은 값의 타입이 올바르지 않을 때
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResponseDto> handleMethodArgTypeException(MethodArgumentTypeMismatchException ex){

        String fieldName = ex.getName();
        String requiredType = ex.getRequiredType().getSimpleName();
        String message = fieldName+"이 "+requiredType+"타입이여야 합니다.";
        log.error("URI값이 올바르지 않습니다. - "+ message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponseDto(message,null));
    }
    
    // 사진용량이 너무 클 시
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<CommonResponseDto> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex){

        String message = "사진용량이 너무 큽니다. 다른사진을 보내주세요.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponseDto(message,null));
    }
    
    // 기타예외 발생 시 500반환
    @ExceptionHandler
    public ResponseEntity<CommonResponseDto> handleException(Exception ex) {

        String message = "서버 내부에 에러가 발생했습니다.";
        log.error(message+":"+ex.getMessage()+ex.getStackTrace()+ex.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CommonResponseDto(message,null));
    }

}
