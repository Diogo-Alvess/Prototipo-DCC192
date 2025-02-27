package edu.dcc192.ex04;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaptchaController {

    @Autowired
    private GeradorSenha geradorSenha; // Para gerar o código da senha

    private String captcha = "12345";  // Um exemplo simples de CAPTCHA estático para validação

    @GetMapping("/captcha")
    public String mostrarCaptcha() {
        // Aqui você pode exibir o CAPTCHA (na verdade, um simples valor ou uma imagem real)
        return "Digite o CAPTCHA: " + captcha;
    }

    @PostMapping("/validarCaptcha")
    public String validarCaptcha(@RequestParam String captchaUsuario) {
        // Verifica se o CAPTCHA inserido pelo usuário é igual ao CAPTCHA armazenado
        if (captchaUsuario.equals(captcha)) {
            return "Captcha validado. Agora, insira seu nome!";
        } else {
            return "Erro no CAPTCHA. Tente novamente.";
        }
    }

    @PostMapping("/saudarUsuario")
    public String saudarUsuario(@RequestParam String nome) {
        // Após a validação do CAPTCHA, agora mostramos a saudação com o nome
        return "Olá " + nome + "! Agora, digite o código para continuar.";
    }

    @PostMapping("/validarCodigo")
    public String validarCodigo(@RequestParam String codigo) {
        // Gerar um código de verificação
        String codigoGerado = geradorSenha.GerarSenha();
        if (codigo.equals(codigoGerado)) {
            return "Código validado com sucesso! Você está pronto para continuar.";
        } else {
            return "Código inválido. Tente novamente.";
        }
    }
}
