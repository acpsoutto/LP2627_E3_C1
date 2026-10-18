package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.apache.commons.validator.routines.EmailValidator;
import org.passay.*; //password e ver uma bibliteca para o email

public class Gerir {
	
	private SessionFactory sessionFactory;
	private Utilizador sessao = null;
	private int tentativasFalhadas = 0;
	
	// Validador da Passay: define as regras da password.
    // É criado uma só vez e reutilizado sempre que for preciso validar.
    private PasswordValidator validador = new PasswordValidator(
        new CharacterRule(EnglishCharacterData.UpperCase, 1),  // pelo menos 1 maiúscula
        new CharacterRule(EnglishCharacterData.Special, 1)
        );
    
    // Validador de emails (Apache Commons Validator): criado uma só vez.
    private EmailValidator validadorEmail = EmailValidator.getInstance();

	public void setup() {
	    sessionFactory = new Configuration()
	            .configure("hibernate.cfg.xml")
	            .buildSessionFactory();
	}
	
	public void exit() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
	
	public void logout() {
		sessao = null;
		tentativasFalhadas = 0;
	}
	
	// Métodos para Filtrar, Imprimir e Reivindicar anúncios
	
	public List<Anuncio> filtrarAnuncios(int op, Categoria categoria, String cor, String titulo) {
        String tipo;
        if (op == 1) {
            tipo = "AnuncioPerdido";
        } else {
            tipo = "AnuncioEncontrado";
        }
        String hql = "select a from " + tipo + " a join fetch a.utilizador where a.estado = :estado order by a.dataPublicacao desc";

        try (Session session = sessionFactory.openSession()) {
            List<Anuncio> lista = session
                    .createQuery(hql, Anuncio.class)
                    .setParameter("estado", Estado.PENDENTE)
                    .getResultList();

            if (categoria != null) {
                lista.removeIf(a -> a.getCategoria() != categoria);
            }
            if (cor != null && !cor.isEmpty()) {
                lista.removeIf(a -> !a.getCor().toLowerCase().contains(cor.toLowerCase()));
            }
            if (titulo != null && !titulo.isEmpty()) {
                lista.removeIf(a -> !a.getTitulo().toLowerCase().contains(titulo.toLowerCase()));
            }

            return lista;
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

		public void reivindicar(long anuncioID, long utiID) {
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
		        
		        if (dono.getIdUtilizador()==utilizador.getIdUtilizador()){
		        	System.out.println("Erro: Não pode reivindicar um anúncio criado por si.");
		        	transaction.rollback();
		        	return;
		        }
		        
		        if (anuncio.getEstado() == Estado.RESOLVIDO) {
		            System.out.println("Este anúncio já foi resolvido.");
		            transaction.rollback();
		            return;
		        }

		        Chat chat = new Chat(anuncio, utilizador);
		        anuncio.addChat(chat);

		        String text;
		        if (anuncio instanceof AnuncioEncontrado) {
		            text = "Olá, acho que encontrou o meu pertence!";
		        } else {
		            text = "Olá, eu acho que encontrei o seu pertence!";
		        }

		        Mensagem mensagem = new Mensagem(chat, utilizador, text);
		        chat.adicionarMensagem(mensagem);
		        
		        session.persist(chat);
		        transaction.commit();
		        System.out.println("Chat iniciado com sucesso! Primeira mensagem enviada:" + text);
			 } catch (Exception e) {
			        if (transaction != null && transaction.isActive()) {
			            transaction.rollback();
			        }
			        e.printStackTrace();
			    }
		}
		
		// enviar nova mensagem num chat
		
		public void enviarMensagem(long idChat, long idEmissor, String texto) {
			if (texto == null || texto.isBlank()) {
				System.out.println("Erro: A mensagem não pode estar vazia");
				return;
			}
			
			Transaction transaction = null;
			try (Session session = sessionFactory.openSession()) {
				transaction = session.beginTransaction();

				Chat chat = session.get(Chat.class, idChat);
				Utilizador emissor = session.get(Utilizador.class, idEmissor);

				if (chat == null || emissor == null) {
					System.out.println("Chat ou utilizador não encontrado.");
					transaction.rollback();
					return;
				}

				if (!chat.pertenceAoChat(emissor)) {
					System.out.println("Erro: Não faz parte deste chat.");
					transaction.rollback();
					return;
				}

				Mensagem novaMsg = new Mensagem(chat, emissor, texto);
				chat.adicionarMensagem(novaMsg);

				session.persist(novaMsg);
				transaction.commit();
				System.out.println("Mensagem enviada!");

				if (chat.getAnuncio().getEstado() == Estado.RESOLVIDO) {
					System.out.println("O anúncio foi marcado como RESOLVIDO em " + chat.getAnuncio().getDataResolucao());
				}
				
			} catch (Exception e) {
				if (transaction != null && transaction.isActive()) {
					transaction.rollback();
				}
				e.printStackTrace();
			}
		}
		
		public List<Chat> listarChats(Utilizador u) {
			List<Chat> meusChats = new ArrayList<>();

			try (Session session = sessionFactory.openSession()) {
				// vai buscar todos os chats à bd (pag49 do tutorial)
				List<Chat> todosOsChats = session
						.createQuery("from Chat", Chat.class)
						.getResultList();

				
				for (Chat c : todosOsChats) {
					if (c.getCriador().getIdUtilizador() == u.getIdUtilizador()
							|| c.getInteressado().getIdUtilizador() == u.getIdUtilizador()) {
						meusChats.add(c);
					}
				}
			}
			return meusChats;
		}
		
		public List<Anuncio> listarAnuncios(Utilizador u) {
            List<Anuncio> meusAnuncios = new ArrayList<>();

            try (Session session = sessionFactory.openSession()) {
                List<Anuncio> todosOsAnuncios = session
                        .createQuery("from Anuncio", Anuncio.class)
                        .getResultList();


                for (Anuncio a : todosOsAnuncios) {
                    if (a.getUtilizador().getIdUtilizador() == u.getIdUtilizador()) {
                        meusAnuncios.add(a);
                    }
                }
            }
            return meusAnuncios;
        }
		
		public void imprimirChats(List<Chat> listaChats) {
			if (listaChats.isEmpty()) {
				System.out.println("Sem chats.");
			}
			for (Chat c : listaChats) {
				System.out.println(c);
			}
		}
		
		public void mostrarMensagensChat(long idChat) {
			try (Session session = sessionFactory.openSession()){
				Chat c = session.get(Chat.class, idChat);
				if (c==null) {
					System.out.println("Chat não encontrado.");
					return;
				}
				
				System.out.println("\nConversa do Anúncio: " + c.getAnuncio().getTitulo());
				for (Mensagem m : c.getMensagens()) {
					System.out.println(m);
				}
			}
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
		if (!emailValido(email)) {
			throw new Exception("Formato de email inválido (exemplo: nome@dominio.pt).");
		}
        // CA03 e CA04: a password tem de ter maiúscula e carácter especial.
        // Se não tiver, mostramos uma mensagem de erro clara.
		if (!passwordValida(password)) {
            throw new Exception("A password deve ter pelo menos uma maiúscula e um carácter especial.");
        }
		if (procurarPorEmail(email) != null) {
			throw new Exception("Email já registado!");
		}
		
		Transaction t = null;
		try(Session session = sessionFactory.openSession()){
			t = session.beginTransaction();
			session.persist(new Utilizador(nome, email,password, TipoUtilizador.NORMAL));
			t.commit();
		} catch(Exception e) {
			if(t != null && t.isActive()) {
				t.rollback();
			}
			throw e;
		}
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
	
	public Utilizador procurarPorEmail(String email) {
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
    public boolean passwordValida(String password) {
        return validador.validate(new PasswordData(password)).isValid();
    }
    
    // ATUALIZADO (novo metodo): verifica se o email tem formato valido
    // (ex: nome@dominio.pt) e se cabe na coluna da BD (máximo 100 caracteres).
    public boolean emailValido(String email) {
        return email != null
                && email.length() <= 100
                && validadorEmail.isValid(email);
    }
    
// MATHIAS
    //Atualizar dados pessoais
    //US04
    public void atualizarDadosPessoais(long idUtilizador, String novoNome, String novoEmail, String novaPassword) {
    	// ATUALIZADO: validaçao do formato do novo email (nao existia antes).
    	if (!emailValido(novoEmail)) {
             System.out.println("Erro: Formato de email inválido (exemplo: nome@dominio.pt).");
             return;
        }
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
        try (Session session = sessionFactory.openSession()) {//fecha a sessão automaticamente no fim, aconteça o que acontecer.
            transaction = session.beginTransaction(); // abre a transacao

            Utilizador u = session.get(Utilizador.class, idUtilizador);
            if (u != null) {
                u.setNome(novoNome);
                u.setEmail(novoEmail);
                u.setPassword(novaPassword);
                // ATUALIZADO: o commit passou para aqui, antes da mensagem de sucesso,
                // para só dizer "sucesso" se a gravação na BD correr bem.
                transaction.commit(); //commit quer dizer confirmar
                
                // ATUALIZADO (correção do erro): atualiza também o utilizador em sessão.
                // Antes só a BD era alterada, e o getPerfil() continuava a devolver
                // os dados antigos (nome, email e password) até haver novo login.
                if (sessao != null && sessao.getIdUtilizador() == idUtilizador) {
                    sessao.setNome(novoNome);
                    sessao.setEmail(novoEmail);
                    sessao.setPassword(novaPassword);
                }
                System.out.println("Dados atualizados com sucesso!");
                
            } else {
                // ATUALIZADO: se o utilizador não existe, faz rollback
                // em vez de um commit sem alterações.
            	transaction.rollback();//rollback quer dizer anular
                System.out.println("Utilizador não encontrado.");
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback(); //rollback quer dizer anular
            }
            e.printStackTrace(); }//mostra onde e porquê falhou.
    }
//printStackTrace() escreve na consola a mensagem do erro e esse caminho, por exemplo:

//org.hibernate.exception.ConstraintViolationException: Duplicate entry 'ana@upt.pt'
//at upt.lp.objetosperdidos.Gerir.atualizarDadosPessoais(Gerir.java:312)
//at upt.lp.objetosperdidos.Teste.main(Teste.java:180)       
     
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
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    public void eliminarAnuncio(long idAnuncio) {
		Transaction transaction = null;
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();
			Anuncio anuncio = session.get(Anuncio.class, idAnuncio);
			if (anuncio != null) {
				session.remove(anuncio);
				System.out.println("Anúncio #" + idAnuncio + " eliminado com sucesso.");
			} else {
				System.out.println("Anúncio não encontrado.");
			}
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
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
