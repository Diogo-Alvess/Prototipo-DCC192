package edu.dcc192.ex04;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Valida {

    @Autowired
    private UsuarioRepository ur;

    // Valida o nome e a senha
    public boolean testa(String nome, String senha) {
        List<Usuario> lu = ur.findAll();
        boolean achou = false;
        for (Usuario i : lu) {
            if (i.getNome().equals(nome) && i.getSenha().equals(senha)) {
                achou = true;
                break;
            }
        }
        return achou;
    }

    // Verifica se o nome já existe
    public boolean existe(String nome) {
        List<Usuario> lu = ur.findAll();
        boolean achou = false;
        for (Usuario i : lu) {
            if (i.getNome().equals(nome)) {
                achou = true;
                break;
            }
        }
        return achou;
    }
}
