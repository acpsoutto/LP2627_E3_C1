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

/**
 * Classe central da lógica da aplicação de objetos perdidos.
 * <p>
 * Contém as operações sobre a base de dados (através do Hibernate) e a gestão do
 * utilizador autenticado. Agrupa:
 * <ul>
 *   <li>utilizadores: registo, login, perfil e atualização de dados pessoais;</li>
 *   <li>anúncios: criação, filtragem, listagem e eliminação;</li>
 *   <li>chats e mensagens: reivindicar um anúncio, listar chats e enviar mensagens;</li>
 *   <li>estatísticas para o gestor.</li>
 * </ul>
 */
public class Gerir {
	
	/** Fábrica de sessões do Hibernate, criada em {@link #setup()}. */
	private SessionFactory sessionFactory;
	/** Utilizador autenticado neste momento, ou {@code null} se ninguém tiver feito login. */
	private Utilizador sessao = null;
	/** Número de tentativas de login falhadas seguidas. */
	private int tentativasFalhadas = 0;
	
	// Validador da Passay: define as regras da password.
    // É criado uma só vez e reutilizado sempre que for preciso validar.
    private PasswordValidator validador = new PasswordValidator(
        new CharacterRule(EnglishCharacterData.UpperCase, 1),  // pelo menos 1 maiúscula
        new CharacterRule(EnglishCharacterData.Special, 1)
        );
    
    // Validador de emails (Apache Commons Validator): criado uma só vez.
    private EmailValidator validadorEmail = EmailValidator.getInstance();

	/**
	 * Prepara a ligação à base de dados. Deve ser chamado uma vez, no início do programa.
	 */
	public void setup() {
	    sessionFactory = new Configuration()
	            .configure("hibernate.cfg.xml")
	            .buildSessionFactory();
	}
	
	/**
	 * Fecha a ligação à base de dados. Deve ser chamado no fim do programa.
	 */
	public void exit() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
	
	/**
	 * Termina a sessão do utilizador autenticado e limpa o contador de tentativas falhadas.
	 */
	public void logout() {
		sessao = null;
		tentativasFalhadas = 0;
	}
	
	/**
	 * Procura anúncios pendentes, com filtros opcionais, ordenados do mais recente
	 * para o mais antigo.
	 * <p>
	 * Os filtros que forem {@code null} ou vazios são ignorados. A pesquisa por cor
	 * e por título não distingue maiúsculas de minúsculas e aceita texto parcial.
	 *
	 * @param op        tipo de anúncio: 1 para perdidos, qualquer outro valor para encontrados
	 * @param categoria categoria pretendida, ou {@code null} para todas
	 * @param cor       texto a procurar na cor, ou {@code null}/vazio para ignorar
	 * @param titulo    texto a procurar no título, ou {@code null}/vazio para ignorar
	 * @return a lista de anúncios pendentes que cumprem os filtros
	 */
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

	/**
	 * Imprime na consola uma lista de anúncios, um por linha.
	 * Se a lista estiver vazia, escreve "Sem resultados.".
	 *
	 * @param anuncios a lista de anúncios a imprimir
	 */
		public void imprimirAnuncios(List<Anuncio> anuncios) {
		    if (anuncios.isEmpty()) {
		        System.out.println("Sem resultados.");
		    }
		    for (Anuncio a : anuncios) {
		        System.out.println(a);
		    }
		}

		/**
		 * Reivindica um anúncio: cria um chat entre o dono do anúncio e o utilizador
		 * interessado, com uma primeira mensagem automática.
		 * <p>
		 * A operação é recusada (e nada é gravado) se o anúncio ou o utilizador não
		 * existirem, se o utilizador for o dono do anúncio ou se o anúncio já estiver resolvido.
		 *
		 * @param anuncioID identificador do anúncio a reivindicar
		 * @param utiID     identificador do utilizador que reivindica
		 */
		public void reivindicar(int anuncioID, int utiID, int op) {
			Transaction transaction = null;
			try (Session session = sessionFactory.openSession()) {
		        transaction = session.beginTransaction();
		        Anuncio anuncio = session.get(Anuncio.class, anuncioID);
		        Utilizador utilizador = session.get(Utilizador.class, utiID);
		        if ((anuncio instanceof AnuncioPerdido && op==2) ||(anuncio instanceof AnuncioEncontrado && op==1)) {
		        	System.out.println("Anuncio não encontrado");
		        	transaction.rollback();
		        	return;
		        	}

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
		
		/**
		 * Verifica se um identificador de chat existe numa lista de chats.
		 * Serve para garantir que o utilizador só abre chats que lhe pertencem.
		 *
		 * @param idChat   identificador do chat a procurar
		 * @param meusChats lista de chats do utilizador
		 * @return {@code true} se o chat estiver na lista, {@code false} caso contrário
		 */
		public boolean verificarIdChat(int idChat, List<Chat> meusChats) {
			for(Chat c : meusChats) {
				if(idChat == c.getIdChat()) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Envia uma nova mensagem num chat.
		 * <p>
		 * A mensagem é recusada se estiver vazia, se o chat ou o emissor não existirem
		 * ou se o emissor não fizer parte do chat. Se o dono do anúncio escrever
		 * "Este anuncio ficou resolvido", o anúncio fica resolvido
		 *
		 * @param idChat    identificador do chat
		 * @param idEmissor identificador do utilizador que envia a mensagem
		 * @param texto     texto da mensagem
		 */
		public void enviarMensagem(int idChat, int idEmissor, String texto) {
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
		
		/**
		 * Lista os chats em que um utilizador participa, como dono do anúncio
		 * ou como interessado, que estejam no estado pendente.
		 *
		 * @param u o utilizador
		 * @return a lista de chats do utilizador (vazia se não tiver nenhum)
		 */
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
						if(c.getAnuncio().getEstado() == Estado.PENDENTE) {
							meusChats.add(c);
						}
					}
				}
			}
			return meusChats;
		}
		
		/**
		 * Lista os anúncios publicados por um utilizador (em qualquer estado).
		 *
		 * @param u o utilizador
		 * @return a lista de anúncios do utilizador (vazia se não tiver nenhum)
		 */
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
		
		/**
		 * Imprime na consola uma lista de chats, um por linha.
		 * Se a lista estiver vazia, escreve "Sem chats.".
		 *
		 * @param listaChats a lista de chats a imprimir
		 */
		public void imprimirChats(List<Chat> listaChats) {
			if (listaChats.isEmpty()) {
				System.out.println("Sem chats.");
			}
			for (Chat c : listaChats) {
				System.out.println(c);
			}
		}
		
		/**
		 * Mostra na consola todas as mensagens de um chat, com o título do anúncio no topo.
		 * Se o chat não existir, escreve "Chat não encontrado.".
		 *
		 * @param idChat identificador do chat
		 */
		public void mostrarMensagensChat(int idChat) {
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
		
	/**
	 * Regista uma nova conta de utilizador do tipo {@link TipoUtilizador#NORMAL}.
	 * <p>
	 * Valida, por esta ordem: campos obrigatórios, formato do email, regras da
	 * password e se o email já está registado.
	 *
	 * @param nome     nome do utilizador
	 * @param email    email do utilizador (formato válido e ainda não registado)
	 * @param password password (pelo menos uma maiúscula e um carácter especial)
	 * @throws Exception se alguma validação falhar ou se a gravação na base de dados falhar
	 */
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
	
	/**
	 * Autentica um utilizador com email e password e guarda-o como utilizador em sessão.
	 * <p>
	 * Se o email não existir ou a password estiver errada, a mensagem é sempre
	 * "Credenciais inválidas". À terceira tentativa falhada seguida, o contador volta
	 * a zero e é lançada uma exceção a indicar o regresso ao login.
	 *
	 * @param email    email do utilizador
	 * @param password password do utilizador
	 * @return o utilizador autenticado (o chamador pode verificar o tipo com {@link Utilizador#getTipo()})
	 * @throws Exception se as credenciais forem inválidas
	 */
	public Utilizador autenticar (String email, String password)throws Exception{
		Utilizador u;
		try(Session session = sessionFactory.openSession()){
			u=procurarPorEmail(email);
		}
		
		if (u== null|| !u.getPassword().equals(password)) {
			tentativasFalhadas++;
			if (tentativasFalhadas == 3) {
				tentativasFalhadas =0;
				throw new Exception ("Três tentativas falhadas. A regressar ao login.");
			}
			throw new Exception("Credencias inválidas.");
		}
        tentativasFalhadas = 0;  
        sessao = u;               
        return u;
	}
	
	/**
	 * Devolve o utilizador autenticado (o perfil).
	 *
	 * @return o utilizador em sessão, ou {@code null} se ninguém tiver feito login
	 */ 
	public Utilizador getPerfil() {
		return sessao;
	}
	
//METODO AUXILIARES (so usados dentro desta classe, por isso 'private')
	
	/**
	 * Procura na base de dados o utilizador com um determinado email.
	 *
	 * @param email o email a procurar
	 * @return o utilizador encontrado, ou {@code null} se não existir
	 */
	public Utilizador procurarPorEmail(String email) {
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

    /**
     * Verifica se uma password cumpre as regras: pelo menos uma maiúscula e
     * pelo menos um carácter especial.
     *
     * @param password a password a verificar
     * @return true se a password for válida, false caso contrário
     */
    public boolean passwordValida(String password) {
        return validador.validate(new PasswordData(password)).isValid();
    }
    
    /**
     * Verifica se um email tem formato válido (por exemplo, {@code nome@dominio.pt})
     * e se cabe na coluna da base de dados (máximo de 100 caracteres).
     *
     * @param email o email a verificar
     * @return {@code true} se o email for válido, {@code false} caso contrário
     *         (incluindo quando é {@code null})
     */
    public boolean emailValido(String email) {
        return email != null
                && email.length() <= 100
                && validadorEmail.isValid(email);
    }
    
    /**
     * Atualiza o nome, o email e a password de um utilizador.
     * <p>
     * A atualização é recusada (e nada é gravado) se o email tiver formato inválido,
     * se a password não cumprir as regras ou se o novo email já pertencer a outro utilizador.
     * Se o utilizador atualizado for o que está em sessão, os dados da sessão também
     * são atualizados, para o perfil mostrar logo os valores novos.
     *
     * @param idUtilizador identificador do utilizador a atualizar
     * @param novoNome     novo nome
     * @param novoEmail    novo email (formato válido e não usado por outro utilizador)
     * @param novaPassword nova password (pelo menos uma maiúscula e um carácter especial)
     */
    public void atualizarDadosPessoais(int idUtilizador, String novoNome, String novoEmail, String novaPassword) {
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
        try (Session session = sessionFactory.openSession()) {
        	transaction = session.beginTransaction();
            Utilizador u = session.get(Utilizador.class, idUtilizador);
            if (u != null) {
                u.setNome(novoNome);
                u.setEmail(novoEmail);
                u.setPassword(novaPassword);

                transaction.commit(); 
                
                if (sessao != null && sessao.getIdUtilizador() == idUtilizador) {
                    sessao.setNome(novoNome);
                    sessao.setEmail(novoEmail);
                    sessao.setPassword(novaPassword);
                }
                System.out.println("Dados atualizados com sucesso!");
                
            } else {
            	transaction.rollback();
                System.out.println("Utilizador não encontrado.");
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback(); 
            }
            e.printStackTrace(); }
    }
//printStackTrace() escreve na consola a mensagem do erro e esse caminho, por exemplo:

//org.hibernate.exception.ConstraintViolationException: Duplicate entry 'ana@upt.pt'
//at upt.lp.objetosperdidos.Gerir.atualizarDadosPessoais(Gerir.java:312)
//at upt.lp.objetosperdidos.Teste.main(Teste.java:180)       
     
    /**
     * Cria um anúncio de objeto encontrado, publicado pelo utilizador em sessão.
     * <p>
     * É recusado se ninguém tiver feito login ou se o título ou a localização
     * estiverem vazios.
     *
     * @param titulo      título do anúncio (obrigatório)
     * @param categoria   categoria do objeto
     * @param descricao   descrição do objeto
     * @param cor         cor do objeto
     * @param localizacao local onde o objeto foi encontrado (obrigatório)
     */
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
    
    /**
     * Cria um anúncio de objeto perdido, publicado pelo utilizador em sessão.
     * <p>
     * É recusado se ninguém tiver feito login ou se o título ou a descrição
     * estiverem vazios.
     *
     * @param titulo    título do anúncio (obrigatório)
     * @param categoria categoria do objeto
     * @param descricao descrição do objeto (obrigatória)
     * @param cor       cor do objeto
     * @param dataPerda data em que o objeto foi perdido
     */
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
    
    /**
     * Elimina um anúncio da base de dados, juntamente com os chats e mensagens associados.
     * Se o anúncio não existir, escreve "Anúncio não encontrado.".
     *
     * @param idAnuncio identificador do anúncio a eliminar
     */
    public void eliminarAnuncio(int idAnuncio) {
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
    
    /**
     * Mostra na consola as estatísticas dos anúncios publicados num intervalo de datas
     * (inclusive): número de objetos perdidos, de objetos encontrados e de anúncios
     * resolvidos.
     *
     * @param inicio primeira data do intervalo
     * @param fim    última data do intervalo
     */
    public void mostrarEstatisticas(LocalDate inicio, LocalDate fim) {
    	// Validar se as datas são coerentes
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException(
                    "A data de início não pode ser posterior à data de fim.");
        }
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