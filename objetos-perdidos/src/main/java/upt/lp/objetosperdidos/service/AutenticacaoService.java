package upt.lp.objetosperdidos.service;

import java.util.ArrayList;
import org.passay.*;

//import dos packages
import upt.lp.objetosperdidos.model.*; //TipoUtilizador e Utilizador

//Esta classe contém a lógica das minhas User Stories:
//US1 - registar conta
//US2 - autenticar (login)
//US3 - ver os dados do perfil
public class AutenticacaoService {
	private ArrayList<Utilizador> utilizadores = new ArrayList<>();
	private Utilizador sessao = null;
	private int tentativasFalhadas = 0; //nao esta do diagrama
	// Validador da Passay: define as regras da password.
    // É criado uma só vez e reutilizado sempre que for preciso validar.
    private PasswordValidator validador = new PasswordValidator(
        new CharacterRule(EnglishCharacterData.UpperCase, 1),  // pelo menos 1 maiúscula
        new CharacterRule(EnglishCharacterData.Special, 1)
        );
	
	//US1: registrar uma nova conta
	//-----------------------------------------------------
	public void registrar (String nome, String email, String password)throws Exception{
	    // CA01: nome, email e password são obrigatórios.
        // isBlank() devolve true se o texto estiver vazio ou só com espaços.
		if (nome.isBlank() || email.isBlank()|| password.isBlank()) {
			throw new Exception("Nome, email e password são obrigatorios.");	
		}
		// CA02: não pode haver duas contas com o mesmo email.
        // procurarPorEmail devolve null se o email não existir ainda.	
		if (procurarPorEmail(email)!= null) {
			throw new Exception("Email já registrado!");
		}
        // CA03 e CA04: a password tem de ter maiúscula e carácter especial.
        // Se não tiver, mostramos uma mensagem de erro clara.
		if (!passwordValida(password)) {
            throw new Exception("A password deve ter pelo menos uma maiúscula e um carácter especial.");
        }
		// Se passou todas as verificações, criamos o utilizador.
        // O id é o tamanho da lista + 1 (1.º utilizador = 1, 2.º = 2, ...).
        // Quem se regista fica sempre como tipo Normal.
        int id = utilizadores.size() + 1;
        Utilizador novo = new Utilizador(id, nome, email, password, TipoUtilizador.Normal);
        utilizadores.add(novo);
	}
	// US2: AUTENTICAR (LOGIN)
	//------------------------------------------
	public Utilizador autenticar (String email, String password)throws Exception{
		// Procura o utilizador pelo email (null se não existir).
		Utilizador u= procurarPorEmail(email);
		
		// CA03: se o email não existe OU a password está errada,
        // a mensagem é sempre "Credenciais inválidas".
        // O "!" significa "não": !igual = diferente.
		if (u== null|| !u.getPassword().equals(password)) {
			tentativasFalhadas++;
			// CA02: à terceira vez, o utilizador volta ao ecrã de login.
			if (tentativasFalhadas == 3) {
				tentativasFalhadas =0;
				throw new Exception ("Três tentativas falhadas. A regressar ao login.");
			}
			throw new Exception("Credencias inválidas.");
		}
		// Se chegou aqui, o login está certo.
        tentativasFalhadas = 0;   // limpa o contador de erros
        sessao = u;               // guarda quem está autenticado

        // CA04: quem chamou este método verifica o tipo.
        // Exemplo: if (u.getTipo() == TipoU.Gestor) { abrir menu de gestor }
        return u;
	}
	//US3:
	//------------------------------------------
	//Devolve o utilizador 
	public Utilizador getPerfil() {
		return sessao;
	}
	//METODO AUXILIARES (so usados dentro desta classe, por isso 'private')
	
	//Pecorrer a lista e devolve o utilizador com este email
	//se nao encontrar nenhum, devolva null
	
	private Utilizador procurarPorEmail(String email) {
		//Ciclo "para cada": u toma o valor de cada utilizador da lista, de 
		for (Utilizador u: utilizadores) {
			if (u.getEmail().equalsIgnoreCase(email)) {
				return u;
			}
		}
		return null;
	}

    // Pede à Passay para verificar a password.
    // new PasswordData(password) embrulha o texto no formato que a Passay precisa.
    // validate(...) aplica as regras; isValid() devolve true se cumpriu todas.
    private boolean passwordValida(String password) {
        return validador.validate(new PasswordData(password)).isValid();
    }
}


