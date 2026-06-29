package br.com.lucas.candidaturaestagio.service;

import br.com.lucas.candidaturaestagio.model.Candidato;
import br.com.lucas.candidaturaestagio.repository.CandidatoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CandidatoService {
    
    private final CandidatoRepository candidatoRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CandidatoService(CandidatoRepository candidatoRepository, PasswordEncoder passwordEncoder) {
        this.candidatoRepository = candidatoRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Candidato registrar(Candidato candidato) {
        // Criptografar senha antes de salvar
        candidato.setSenha(passwordEncoder.encode(candidato.getSenha()));
        return candidatoRepository.save(candidato);
    }
    
    public boolean validarCredenciais(String email, String senha) {
        Optional<Candidato> candidatoOpt = candidatoRepository.findByEmail(email);
        if (candidatoOpt.isEmpty()) {
            return false;
        }
        // Comparar senha fornecida com a criptografada no banco
        return passwordEncoder.matches(senha, candidatoOpt.get().getSenha());
    }
    
    public Optional<Candidato> obterPorEmail(String email) {
        return candidatoRepository.findByEmail(email);
    }
}
