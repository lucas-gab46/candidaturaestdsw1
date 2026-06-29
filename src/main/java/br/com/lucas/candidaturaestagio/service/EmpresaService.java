package br.com.lucas.candidaturaestagio.service;

import br.com.lucas.candidaturaestagio.model.Empresa;
import br.com.lucas.candidaturaestagio.repository.EmpresaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EmpresaService {
    
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    
    public EmpresaService(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Empresa registrar(Empresa empresa) {
        // Criptografar senha antes de salvar
        empresa.setSenha(passwordEncoder.encode(empresa.getSenha()));
        return empresaRepository.save(empresa);
    }
    
    public boolean validarCredenciais(String email, String senha) {
        Optional<Empresa> empresaOpt = empresaRepository.findByEmail(email);
        if (empresaOpt.isEmpty()) {
            return false;
        }
        // Comparar senha fornecida com a criptografada no banco
        return passwordEncoder.matches(senha, empresaOpt.get().getSenha());
    }
    
    public Optional<Empresa> obterPorEmail(String email) {
        return empresaRepository.findByEmail(email);
    }
}
