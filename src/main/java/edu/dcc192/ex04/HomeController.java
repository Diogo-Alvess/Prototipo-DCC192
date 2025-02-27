package edu.dcc192.ex04;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;



@Controller
public class HomeController {


@Autowired
private Valida valida;

@Autowired
private UsuarioRepository ur;  // Injeção do repositório UsuarioRepository


private JSONArray records = new JSONArray();
    private String filePath = "./src/main/resources/loginRecords.json";  // Arquivo JSON para armazenar os logins


    // Carrega o arquivo JSON ou cria um novo, se não existir
    public void loadLoginRecords() {
    try {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        records = new JSONArray(new String(bytes));
    } catch (IOException e) {
        System.out.println("Erro ao carregar o arquivo JSON, criando novo arquivo.");
        records = new JSONArray();  // Caso o arquivo não exista, inicializa uma lista vazia
    }
}

    

    public void saveLoginRecords() {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(records.toString(4));  // Escreve no arquivo JSON com indentação
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo JSON.");
        }
    }

    // Retorna a data no formato dd/MM
    public String todayDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        return currentDate.format(formatter);
    }

@GetMapping("/nome")
public ModelAndView nomePage() {
    return new ModelAndView("nome"); // Exibe a página de login
}

@PostMapping("/login")
public String login(@RequestParam String nome, @RequestParam String senha, HttpSession session, Model model) {
    loadLoginRecords();

    // Validação de login (substitua pelo código do seu método de validação real)
    if (valida.testa(nome, senha)) {
        session.setAttribute("nome", nome);  // Armazena o nome na sessão
        String today = todayDate();

        // Verifica se já existe o nome do usuário no JSON
        boolean userExists = false;
        for (int i = 0; i < records.length(); i++) {
            JSONObject userRecord = records.getJSONObject(i);
            if (userRecord.getString("nome").equals(nome)) {
                userExists = true;
                // Verifica se é o mesmo dia
                if (userRecord.has(today)) {
                    int logins = userRecord.getInt(today) + 1;
                    userRecord.put(today, logins);  // Incrementa o login do dia
                } else {
                    userRecord.put(today, 1);  // Se não for o mesmo dia, inicia com 1 login
                }
                break;
            }
        }

        // Se o usuário não foi encontrado, adiciona um novo registro
        if (!userExists) {
            JSONObject newUser = new JSONObject();
            newUser.put("nome", nome);
            newUser.put(today, 1);  // Primeira vez logando no dia
            records.put(newUser);
        }

        saveLoginRecords();  // Salva as alterações no arquivo JSON

        return "redirect:/hero";  // Redireciona para a página principal
    } else {
        model.addAttribute("mensagem", "Credenciais inválidas. Tente novamente.");
        return "nome";  // Retorna para a página de login
    }
}

@GetMapping("/dashboard")
public String dashboard(Model model) {
    loadLoginRecords();

    // Criando uma lista de mapas com os dados formatados
    List<Map<String, Object>> formattedData = new ArrayList<>();
    for (int i = 0; i < records.length(); i++) {
        JSONObject userRecord = records.getJSONObject(i);
        Map<String, Object> record = new HashMap<>();
        
        // Nome do usuário
        String nome = userRecord.getString("nome");
        
        // Map para armazenar contagens de login por data
        Map<String, Integer> loginCounts = new HashMap<>();
        
        // Adicionando as contagens de login para cada data (exceto o nome)
        for (String date : userRecord.keySet()) {
            if (!"nome".equals(date)) {
                loginCounts.put(date, userRecord.getInt(date));
            }
        }
        
        // Adiciona o nome e os logins ao mapa de dados
        record.put("nome", nome);
        record.put("logins", loginCounts);
        
        // Adiciona o registro formatado à lista
        formattedData.add(record);
    }

    // Passa a lista de dados formatados para o modelo
    model.addAttribute("loginData", formattedData);
    
    // Retorna a página do dashboard
    return "dashboard";  // Página do dashboard
}



@GetMapping("/")
public String homeRedirect() {
    return "redirect:/nome";
}

@GetMapping("/logout")
public String logout(HttpSession session) {
    // Invalidar a sessão do usuário
    session.invalidate();

    // Redireciona para a página de login
    return "redirect:/nome";  // Aqui "nome" é a URL da página de login
}


@GetMapping("/hero")
public ModelAndView heroPage(HttpSession session) {
    String nome = (String) session.getAttribute("nome");
    if (nome == null) {
        nome = "Visitante"; // Caso o nome não tenha sido encontrado na sessão
    }
    
    ModelAndView mv = new ModelAndView();
    mv.setViewName("hero");
    mv.addObject("nome", nome);  // Passa o nome como atributo para a view
    return mv;
}


@GetMapping("/formulario-usuario")
public ModelAndView formularioUsuarioPage() {
    ModelAndView mv = new ModelAndView("formulario-usuario");
    mv.addObject("usuario", new Usuario());
    return mv;
}

@PostMapping("/salvar-usuario")
public ModelAndView salvarUsuario(@RequestParam String nome, @RequestParam String senha) {
    Usuario usuario = new Usuario();
    usuario.setNome(nome);
    usuario.setSenha(senha);
    
    ur.save(usuario);  // Salva o usuário no banco de dados
    
    // Redireciona para a página de login com o parâmetro 'nome' na URL
    ModelAndView mv = new ModelAndView("redirect:/nome?nome=" + nome);  // Adiciona o nome como parâmetro
    mv.addObject("usuario", usuario);
    return mv;
}


@Autowired
    private Dados dados;

    @GetMapping("/menu")
public ModelAndView home2(HttpSession session) {
    String nome = (String) session.getAttribute("nome");
    System.out.println("Nome na sessão: " + nome); // Log para depuração

    if (nome == null) {
        nome = "Visitante";
    }

    ModelAndView mv = new ModelAndView();
    mv.setViewName("menu");
    mv.addObject("nome", nome);
    mv.addObject("dados", dados.pegaDados());
    return mv;
}
    

@GetMapping("/info")
public ModelAndView infoPage(HttpSession session) {
    // Recuperar o nome da sessão
    String nome = (String) session.getAttribute("nome");
    if (nome == null) {
        nome = "Visitante"; // Caso o nome não tenha sido encontrado na sessão
    }

    ModelAndView mv = new ModelAndView();
    mv.setViewName("info"); // Define o nome da página "info.html"
    mv.addObject("nome", nome); // Passa o nome como atributo para a view
    return mv;
}

 // Página de erro HTML
 @GetMapping("/error.html")
 public String erroHtmlPage() {
     return "error"; // Nome do template da página de erro HTML
 }

/*@Autowired
    private GeradorSenha senha;

    private String captchaAtual; // CAPTCHA gerado dinamicamente

    // Método para gerar o CAPTCHA aleatório
    private String gerarCaptcha() {
        Random random = new Random();
        StringBuilder captchaBuilder = new StringBuilder();
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < 5; i++) { // Gera um CAPTCHA de 5 caracteres
            int index = random.nextInt(caracteres.length());
            captchaBuilder.append(caracteres.charAt(index));
        }

        return captchaBuilder.toString();
    }

    @GetMapping("/")
    public ModelAndView home1() {
        ModelAndView mv = new ModelAndView();
        captchaAtual = gerarCaptcha(); // Gerar um CAPTCHA aleatório
        mv.setViewName("login");
        mv.addObject("captcha", captchaAtual); // Passa o CAPTCHA para o HTML
        mv.addObject("senha", senha.GerarSenha()); // Passa a senha gerada
        return mv;
    }

    @GetMapping("/nome")
    public ModelAndView nomePage(@RequestParam String captcha) {
        ModelAndView mv = new ModelAndView();

        // Verifica se o CAPTCHA fornecido corresponde ao gerado
        if (!captcha.equals(captchaAtual)) {
            mv.setViewName("login");
            mv.addObject("erro", "Captcha inválido. Tente novamente!");
            mv.addObject("captcha", captchaAtual); // Gera um novo CAPTCHA
            mv.addObject("senha", senha.GerarSenha());
            return mv;
        }

        mv.setViewName("nome");
        return mv;
    }

    @Autowired
    private Dados dados;

    @GetMapping("/hero")
    public ModelAndView heroPage(@RequestParam(required = false, defaultValue = "Visitante") String nome) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("hero");
        mv.addObject("nome", nome);
        return mv;
    }

    @GetMapping("/menu")
    public ModelAndView home2(@RequestParam String nome) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("menu"); // Define o nome da página que será renderizada, neste caso, "menu.html".
        mv.addObject("nome", nome);// Adiciona o parâmetro "nome" ao modelo, permitindo que ele seja acessado na página de exibição.
        mv.addObject("dados", dados.pegaDados()); // Dados a serem exibidos no menu
        return mv;
    }

    @GetMapping("/info")
    public ModelAndView infoPage(@RequestParam String nome) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("info"); // Define o nome da página que será renderizada, neste caso, "info.html".
        mv.addObject("nome", nome); // Adiciona o parâmetro "nome" ao modelo, permitindo que ele seja acessado na página de exibição.
        return mv;
    }*/

    /*@Autowired
    private GeradorSenha senha;

    @GetMapping("/")
    public String redirecionarParaNome() {
        return "redirect:/nome"; // Redireciona diretamente para a rota /nome
    }

    @GetMapping("/nome")
    public ModelAndView nomePage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("nome");
        mv.addObject("senha", senha.GerarSenha()); // Adiciona a senha gerada ao modelo
        return mv;
    }

    @Autowired
    private Dados dados;

    @GetMapping("/hero")
    public ModelAndView heroPage(@RequestParam(required = false, defaultValue = "Visitante") String nome) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("hero");
        mv.addObject("nome", nome);
        return mv;
    }

    @GetMapping("/menu")
    public ModelAndView home2(@RequestParam String nome) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("menu"); // Define o nome da página que será renderizada, neste caso, "menu.html".
        mv.addObject("nome", nome); // Adiciona o parâmetro "nome" ao modelo, permitindo que ele seja acessado na página de exibição.
        mv.addObject("dados", dados.pegaDados()); // Dados a serem exibidos no menu
        return mv;
    }

    @GetMapping("/info")
    public ModelAndView infoPage(@RequestParam String nome) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("info"); // Define o nome da página que será renderizada, neste caso, "info.html".
        mv.addObject("nome", nome); // Adiciona o parâmetro "nome" ao modelo, permitindo que ele seja acessado na página de exibição.
        return mv;
    }

    @Autowired
    UsuarioRepository ur;

    @RequestMapping({"/usuarios"})
    public ModelAndView requestMethodName() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("usuarios");
        List<Usuario> lu = ur.findAll();
        mv.addObject("usuarios", lu);
        return mv;
    }

    @RequestMapping({"/novo-usuario"})
    public ModelAndView novoUsuario() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("formulario-usuario");
        mv.addObject("usuario", new Usuario());
        return mv;
    }

    @PostMapping("/salvar-usuario")
public String salvarUsuario(@ModelAttribute Usuario usuario) {
    ur.save(usuario); // Salva o usuário no banco
    return "redirect:/usuarios"; // Redireciona para a página com a lista de usuários
}*/


/*@Autowired
    UsuarioRepository ur;

    // Exibe a página de login
    @GetMapping("/login2")
    public String login(Model model) {
        return "login2";
    }

    // Processa o login
    @PostMapping("/login2")
    public String processarLogin(@RequestParam String nome, @RequestParam String senha, Model model, HttpSession session) {
        Optional<Usuario> usuario = ur.findByNomeAndSenha(nome, senha);
        if (usuario.isPresent()) {
            session.setAttribute("usuarioLogado", usuario.get());
            return "redirect:/usuarios";
        } else {
            model.addAttribute("erro", "Usuário ou senha inválidos!");
            return "login2";
        }
    }

    // Exibe a lista de usuários (apenas se logado)
    @RequestMapping("/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login2";
        }
        model.addAttribute("usuarios", ur.findAll());
        model.addAttribute("nomeUsuario", usuarioLogado.getNome());
        return "usuarios";
    }

    // Página para adicionar novo usuário
    @RequestMapping("/novo-usuario")
public ModelAndView novoUsuario() {
    ModelAndView mv = new ModelAndView();
    mv.setViewName("formulario-usuario");
    mv.addObject("usuario", new Usuario());
    return mv;
}

    // Processa a criação de um novo usuário
    @PostMapping("/salvar-usuario")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {
        ur.save(usuario);
        return "redirect:/usuarios";
    }

    // Logout
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login2";
    }*/
}
