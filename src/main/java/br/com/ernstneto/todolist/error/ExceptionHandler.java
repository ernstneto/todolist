package br.com.ernstneto.todolist.error;

import org.springframework.http.converter.HttpMessageNotReadableException;

public @interface ExceptionHandler {

    Class<HttpMessageNotReadableException> value();

}
