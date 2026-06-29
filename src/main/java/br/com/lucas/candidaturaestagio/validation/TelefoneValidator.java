package br.com.lucas.candidaturaestagio.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public void initialize(ValidTelefone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Permite valores nulos (use @NotNull se quiser obrigatório)
        if (value == null) {
            return true;
        }
        
        // Remove caracteres não-dígitos
        String digits = value.replaceAll("\\D", "");
        
        // Válido se tiver 10 ou 11 dígitos
        return digits.length() == 10 || digits.length() == 11;
    }
}
