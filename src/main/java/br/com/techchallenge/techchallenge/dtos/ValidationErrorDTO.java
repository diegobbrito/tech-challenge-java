package br.com.techchallenge.techchallenge.dtos;

import java.util.List;

public record ValidationErrorDTO(List<String> errors, int status) {

}
