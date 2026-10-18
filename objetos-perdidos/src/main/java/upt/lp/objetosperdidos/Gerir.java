package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import java.util.ArrayList;
import org.passay.*; //password e ver uma bibliteca para o email

public class Gerir {
	
	private SessionFactory sessionFactory;
	private ArrayList<Chat> chats = new ArrayList<>();
	
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
		        query.setParameter("estado", Estado.PENDENTE);

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
			try (Session session = sessionFactory.openSession()) {
		        transaction = session.beginTransaction();
		        Anuncio anuncio = session.get(Anuncio.class, anuncioID);
		        Utilizador utilizador = session.get(Utilizador.class, utiID);

		        if (anuncio == null || utilizador == null) {
		            System.out.println("Anúncio ou utilizador não encontrado.");
		            transaction.rollback();
		            return;
		        }

		        Utilizador dono = anuncio.getUtilizador();
		        
		        if (anuncio.getEstado() == Estado.RESOLVIDO) {
		            System.out.println("Este anúncio já foi resolvido.");
		            transaction.rollback();
		            return;
		        }

		        Chat chat = new Chat(anuncio, utilizador);
		        //FAZER Ligação de anuncio.addChat(chat);

		        String text;
		        if (anuncio instanceof AnuncioEncontrado) {
		            text = "Olá, acho que encontrou o meu pertence!";
		        } else {
		            text = "Olá, eu acho que encontrei o seu pertence!";
		        }

		        Mensagem mensagem = new Mensagem(chat, utilizador, text);
		        chat.adicionarMensagem(mensagem);
		        transaction.commit();	
			 } catch (Exception e) {
			        if (transaction != null && transaction.isActive()) {
			            transaction.rollback();
			        }
			        e.printStackTrace();
			        return;
			    }
		}
		
		public void imprimirChats(ArrayList<Chat> chats) {
			if (chats.isEmpty()) {
				System.out.println("Sem chats.");
			}
			for (Chat c : chats) {
				System.out.println(c.getIdChat() + " - " + c.getAnuncio().getTitulo());
			}
		}
		
		public ArrayList<Chat> listarChats(Utilizador u) {
			ArrayList<Chat> meusChats = new ArrayList<>();
			for (Chat c : chats) {
				if (c.getCriador().getIdUtilizador() == u.getIdUtilizador()
						|| c.getInteressado().getIdUtilizador() == u.getIdUtilizador()) {
					meusChats.add(c);
				}
			}
			return meusChats;
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
        
        Utilizador novo = new Utilizador(nome, email, password, TipoUtilizador.Normal);
        utilizadores.add(novo);

		
		try(Session session = sessionFactory.openSession()){
			if (procurarPorEmail(email)!= null) {
				throw new Exception("Email já registrado!");
			}
			Transaction t = session.beginTransaction();
			session.persist(new Utilizador(nome, email,password, TipoUtilizador.Normal));
	}
	// US2: AUTENTICAR (LOGIN)
	//------------------------------------------
	public Utilizador autenticar (String email, String password)throws Exception{
		// Procura o utilizador pelo email (null se não existir).
		Utilizador u;
		try(Session session = sessionFactory.openSession()){
			u=procurarPorEmail(email);
		}
		
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
		//Procurar mediante a BD
		try (Session session = sessionFactory.openSession()) {
            List<Utilizador> lista = session
                    .createQuery("from Utilizador u where u.email = :email", Utilizador.class)
                    .setParameter("email", email)
                    .getResultList();

            if (lista.isEmpty()) {
                return null;
            }
            return lista.get(0);
        }
	}

    // Pede à Passay para verificar a password.
    // new PasswordData(password) embrulha o texto no formato que a Passay precisa.
    // validate(...) aplica as regras; isValid() devolve true se cumpriu todas.
    private boolean passwordValida(String password) {
        return validador.validate(new PasswordData(password)).isValid();
    }
    
// MATHIAS
    //Atualizar dados pessoais
    //US04
    public void atualizarDadosPessoais(int idUtilizador, String novoNome, String novoEmail, String novaPassword) {
        if (!passwordValida(novaPassword)) {
            System.out.println("Erro: A nova password deve conter uma maiúscula e um carácter especial.");
            return;
        }

        Utilizador existente = procurarPorEmail(novoEmail);
        if (existente != null && existente.getIdUtilizador() != idUtilizador) {
            System.out.println("Erro: O novo email já existe no sistema.");
            return;
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Utilizador u = session.get(Utilizador.class, idUtilizador);
            if (u != null) {
                u.setNome(novoNome);
                u.setEmail(novoEmail);
                u.setPassword(novaPassword);
                System.out.println("Dados atualizados com sucesso!");
            } else {
                System.out.println("Utilizador não encontrado.");
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // US07: Registar objeto encontrado
    public void criarAnuncioEncontrado(String titulo, Categoria categoria, String descricao, String cor, String localizacao) {
        if (sessao == null) {
            System.out.println("Erro: Deve efetuar login para criar um anúncio.");
            return;
        }

        if (titulo == null || titulo.isBlank() || localizacao == null || localizacao.isBlank()) {
            System.out.println("Erro: O título e a localização são obrigatórios.");
            return;
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Cria o objeto AnuncioEncontrado
            AnuncioEncontrado anuncio = new AnuncioEncontrado(sessao, titulo, categoria, descricao, cor, localizacao);

            // Persiste o anúncio na base de dados
            session.persist(anuncio);

            transaction.commit();
            System.out.println("Anúncio encontrado registado com sucesso!");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // US08: Criar anúncio de objeto perdido
    public void criarAnuncioPerdido(String titulo, Categoria categoria, String descricao, String cor, LocalDate dataPerda) {
        if (sessao == null) {
            System.out.println("Erro: Deve efetuar login para criar um anúncio.");
            return;
        }

        if (titulo == null || titulo.isBlank() || descricao == null || descricao.isBlank()) {
            System.out.println("Erro: O título e a descrição são obrigatórios.");
            return;
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Cria o objeto AnuncioPerdido usando o construtor adequado
            AnuncioPerdido anuncio = new AnuncioPerdido(sessao, titulo, categoria, descricao, cor, dataPerda);
            anuncio.setDataPerda(dataPerda);

            // Persiste o anúncio na base de dados
            session.persist(anuncio);

            transaction.commit();
            System.out.println("Anúncio perdido criado com sucesso!");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    
    //US10
    public void mostrarEstatisticas(LocalDate inicio, LocalDate fim) {
        try (Session session = sessionFactory.openSession()) {
            List<Anuncio> lista = session
                    .createQuery(
                            "from Anuncio a " +
                            "where a.dataPublicacao >= :inicio and a.dataPublicacao <= :fim",
                            Anuncio.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fim", fim)
                    .getResultList();

            int perdidos = 0;
            int encontrados = 0;
            int resolvidos = 0;

            for (Anuncio a : lista) {
                if (a instanceof AnuncioPerdido) {
                    perdidos++;
                } else if (a instanceof AnuncioEncontrado) {
                    encontrados++;
                }
                if (a.getEstado() == Estado.RESOLVIDO) {
                    resolvidos++;
                }
            }

            System.out.println("\n--- Estatísticas (" + inicio + " a " + fim + ") ---");
            System.out.println("Objetos Perdidos: " + perdidos);
            System.out.println("Objetos Encontrados: " + encontrados);
            System.out.println("Objetos com Match (Resolvidos): " + resolvidos);
        }
    }
}
