package br.com.lucas.candidaturaestagio.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<ValidCPF, String> {

    @Override
    public void initialize(ValidCPF constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Permite valores nulos
        if (value == null) {
            return true;
        }
        
        // Remove caracteres não-dígitos
        String cpf = value.replaceAll("\\D", "");
        
        // CPF deve ter 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }
        
        // CPF não pode ter todos os dígitos iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Validar primeiro dígito verificador
        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int remainder1 = sum1 % 11;
        int digit1 = remainder1 < 2 ? 0 : 11 - remainder1;
        
        if (Character.getNumericValue(cpf.charAt(9)) != digit1) {
            return false;
        }
        
        // Validar segundo dígito verificador
        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int remainder2 = sum2 % 11;
        int digit2 = remainder2 < 2 ? 0 : 11 - remainder2;
        
        return Character.getNumericValue(cpf.charAt(10)) == digit2;
    }
}
