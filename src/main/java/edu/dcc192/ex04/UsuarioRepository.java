package edu.dcc192.ex04;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository; // Certifique-se de importar esta anotação
import org.springframework.stereotype.Repository; // Importação do Optional

@Repository

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNomeAndSenha(String nome, String senha);
}
