package br.com.vamatos.product_api.config.exception;


import lombok.Data;

@Data
public class ExceptionDetails {

    private int status;
    private String message;
}
