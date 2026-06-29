package br.com.lucas.candidaturaestagio.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CNPJValidator implements ConstraintValidator<ValidCNPJ, String> {

    @Override
    public void initialize(ValidCNPJ constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Permite valores nulos
        if (value == null) {
            return true;
        }
        
        // Remove caracteres não-dígitos
        String cnpj = value.replaceAll("\\D", "");
        
        // CNPJ deve ter 14 dígitos
        if (cnpj.length() != 14) {
            return false;
        }
        
        // CNPJ não pode ter todos os dígitos iguais
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }
        
        // Validar primeiro dígito verificador
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum1 = 0;
        for (int i = 0; i < 12; i++) {
            sum1 += Character.getNumericValue(cnpj.charAt(i)) * weights1[i];
        }
        int remainder1 = sum1 % 11;
        int digit1 = remainder1 < 2 ? 0 : 11 - remainder1;
        
        if (Character.getNumericValue(cnpj.charAt(12)) != digit1) {
            return false;
        }
        
        // Validar segundo dígito verificador
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum2 = 0;
        for (int i = 0; i < 13; i++) {
            sum2 += Character.getNumericValue(cnpj.charAt(i)) * weights2[i];
        }
        int remainder2 = sum2 % 11;
        int digit2 = remainder2 < 2 ? 0 : 11 - remainder2;
        
        return Character.getNumericValue(cnpj.charAt(13)) == digit2;
    }
}
