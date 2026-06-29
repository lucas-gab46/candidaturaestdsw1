package br.com.lucas.candidaturaestagio.service;

import br.com.lucas.candidaturaestagio.model.Administrador;
import br.com.lucas.candidaturaestagio.repository.AdministradorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdministradorService {
    
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AdministradorService(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Administrador registrar(Administrador administrador) {
        administrador.setSenha(passwordEncoder.encode(administrador.getSenha()));
        return administradorRepository.save(administrador);
    }
    
    public boolean validarCredenciais(String email, String senha) {
        Optional<Administrador> adminOpt = administradorRepository.findByEmail(email);
        if (adminOpt.isEmpty()) {
            return false;
        }
        // Comparar senha fornecida com a criptografada no banco
        return passwordEncoder.matches(senha, adminOpt.get().getSenha());
    }
    
    public Optional<Administrador> obterPorEmail(String email) {
        return administradorRepository.findByEmail(email);
    }
}
