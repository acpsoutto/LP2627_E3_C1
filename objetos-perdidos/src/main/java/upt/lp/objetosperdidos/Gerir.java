package upt.lp.objetosperdidos;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import java.util.ArrayList;
import org.passay.*; //password e ver uma bibliteca para o email

public class Gerir {
	
	private SessionFactory sessionFactory;
//UTILIZADOR - alterar 2 sprint
	private ArrayList<Utilizador> utilizadores = new ArrayList<>();
	private Utilizador sessao = null;
	private int tentativasFalhadas = 0; //nao esta do diagrama
	// Validador da Passay: define as regras da password.
    // É criado uma só vez e reutilizado sempre que for preciso validar.
    private PasswordValidator validador = new PasswordValidator(
        new CharacterRule(EnglishCharacterData.UpperCase, 1),  // pelo menos 1 maiúscula
        new CharacterRule(EnglishCharacterData.Special, 1)
        );
	

	public void setup() {
	    sessionFactory = new Configuration()
	            .configure("hibernate.cfg.xml")
	            .buildSessionFactory();
	}
	
	public List<Anuncio> filtrarAnuncios(int op, Categoria categoria, String cor,String titulo) {
		 String tipo;
		    if (op == 1) {
		        tipo = "AnuncioPerdido";
		    } else {
		        tipo = "AnuncioEncontrado";
		    }
		    String hql = "select a from " + tipo + " a join fetch a.publicador where a.estado = :estado";
		    if (categoria != null) {
		        hql = hql + " and a.categoria = :categoria";
		    }
		    if (cor != null && !cor.isEmpty()) {
		        hql = hql + " and lower(a.cor) like :cor";
		    }
		    if (titulo != null && !titulo.isEmpty()) {
		        hql = hql + " and lower(a.titulo) like :titulo";
		    }
		    hql = hql + " order by a.dataPublicacao desc";
		    try (Session session = sessionFactory.openSession()) {
		        Query<Anuncio> query = session.createQuery(hql, Anuncio.class);
		        query.setParameter("estado", Estado.Pendente);

		        if (categoria != null) {
		            query.setParameter("categoria", categoria);
		        }
		        if (cor != null && !cor.isEmpty()) {
		            query.setParameter("cor", "%" + cor.toLowerCase() + "%");
		        }
		        if (titulo != null && !titulo.isEmpty()) {
		            query.setParameter("titulo", "%" + titulo.toLowerCase() + "%");
		        }
		        return query.getResultList();
		    }
		}

		public void imprimirAnuncios(List<Anuncio> anuncios) {
		    if (anuncios.isEmpty()) {
		        System.out.println("Sem resultados.");
		    }
		    for (Anuncio a : anuncios) {
		        System.out.println(a);
		    }
		}

		public void revindicar(long anuncioID, long utiID) {
			Transaction transaction = null;

		        transaction = session.beginTransaction();
		        Anuncio anuncio = session.get(Anuncio.class, anuncioID);
		        Utilizador utilizador = session.get(Utilizador.class, utiID);

		        if (anuncio == null || utilizador == null) {
		            System.out.println("Anúncio ou utilizador não encontrado.");
		            transaction.rollback();
		            return null;
		        }

		        Utilizador dono = anuncio.getPublicador();
		        
		        if (anuncio.getEstado() == Estado.Resolvido) {
		            System.out.println("Este anúncio já foi resolvido.");
		            transaction.rollback();
		            return null;
		        }

		        List<Chat> existentes = session.createQuery("select c from Chat c join c.participantes p "
		                        + "where c.anuncio.id = :anuncioId and p.id = :utilizadorId", Chat.class)
		                .setParameter("anuncioId", anuncioId)
		                .setParameter("utilizadorId", utilizadorId)
		                .getResultList();

		        Chat chat = new Chat(anuncio);
		        chat.addParticipante(dono);
		        chat.addParticipante(utilizador);
		        anuncio.addChat(chat);

		        String text;
		        if (anuncio instanceof AnuncioEncontrado) {
		            text = "Olá, acho que encontrou o meu pertence!";
		        } else {
		            text = "Olá, eu acho que encontrei o seu pertence!";
		        }

		        Mensagem mensagem = new Mensagem(utilizador, texto);
		        chat.addMensagem(mensagem);

		        transaction.commit();
		        return chat;		
		}
	
//ANA - UTILIZADOR----------------------------

//Esta classe contém a lógica das minhas User Stories:
//US1 - registar conta
//US2 - autenticar (login)
//US3 - ver os dados do perfil
	
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
