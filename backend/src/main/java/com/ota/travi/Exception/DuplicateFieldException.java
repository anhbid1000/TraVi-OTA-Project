package com.ota.travi.Exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class DuplicateFieldException extends RuntimeException{
    private  final Map<String,String> fieldErrors;
    public DuplicateFieldException(Map<String,String> fieldErrors){
        super("Duplicate field found");
        this.fieldErrors = fieldErrors;
    }
}