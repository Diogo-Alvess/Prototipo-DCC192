package edu.dcc192.ex04;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class GeradorSenha {

    private static final int TAMANHO_SENHA_RANDOMICA = 8; // Define o tamanho da senha
    private static int seed = 0; // Variável para manter o controle de aleatoriedade
    private final Random random = new Random(); // Objeto Random para gerar números aleatórios

    public String GerarSenha() {
        StringBuilder senha = new StringBuilder();

        for (int count = 0; count < TAMANHO_SENHA_RANDOMICA / 2; count++) {
            // Gera um caractere numérico
            char numero = (char) ('0' + Math.abs(random.nextInt() + seed) % 10); // Gera um número entre '0' e '9'
            // Gera um caractere alfabético (letra minúscula)
            char letra = (char) ('a' + Math.abs(random.nextInt() + seed) % 26); // Gera uma letra entre 'a' e 'z'

            senha.append(numero); // Adiciona o número à senha
            senha.append(letra);   // Adiciona a letra à senha
        }

        // Incrementa a seed para gerar diferentes sequências em cada execução
        seed += 10;
        return senha.toString(); // Retorna a senha gerada
    }
}


