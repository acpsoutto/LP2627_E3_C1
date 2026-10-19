package upt.lp.objetosperdidos;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

/**
 * Representa um utilizador da aplicação de objetos perdidos.
 * <p>
 * Cada utilizador tem um identificador, nome, email, password e um tipo
 * Um utilizador pode publicar vários anúncios e participar em vários chats.
 * <p>
 * Esta classe é uma entidade JPA/Hibernate, mapeada na tabela {@code utilizador}.
 */
@Entity
@Table(name= "utilizador")
public class Utilizador {
	
	/** Identificador único do utilizador (gerado automaticamente pela base de dados). */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idUtilizador")
	private int idUtilizador;
	
	/** Nome do utilizador. Obrigatório. */
	@Column (nullable = false)
	private String nome;
	
	/** Email do utilizador. Obrigatório e único (não pode haver dois emails iguais). */
	@Column(nullable = false, unique =true)// não pode haver dois emails iguais
	private String email;

	/** Password do utilizador. Obrigatória. */	
	@Column(nullable = false)
	private String password;

	/**
	 * Tipo do utilizador (NORMAL ou GESTOR).
	 * Guardado na base de dados como texto, e não como número.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoUtilizador tipo;
	
	/** Anúncios publicados por este utilizador. */
	@OneToMany(mappedBy = "utilizador", cascade = CascadeType.PERSIST)
	private List<Anuncio> anuncios = new ArrayList<>();
	
	/** Chats em que este utilizador participa. */
	@ManyToMany(mappedBy = "utilizadores")
	private List<Chat> chats = new ArrayList<>();
			
	/**
	 * Construtor vazio, necessário para o Hibernate criar instâncias da entidade.
	 */
	public Utilizador() {
	}

	/**
	 * Cria um novo utilizador.
	 *
	 * @param nome     nome do utilizador
	 * @param email    email do utilizador (deve ser único)
	 * @param password password do utilizador
	 * @param tipo     tipo do utilizador (NORMAL ou GESTOR)
	 */
	public Utilizador( String nome, String email, String password, TipoUtilizador tipo) {
		this.nome = nome;
		this.email = email;
		this.password = password;
		this.tipo = tipo;
	}
	
	/**
	 * Devolve o identificador do utilizador.
	 *
	 * @return o id do utilizador
	 */
	public int getIdUtilizador() {
		return idUtilizador;
	}
	
	/**
	 * Devolve o nome do utilizador.
	 *
	 * @return o nome do utilizador
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * Devolve o email do utilizador.
	 *
	 * @return o email do utilizador
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Devolve a password do utilizador.
	 *
	 * @return a password do utilizador
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Devolve o tipo do utilizador.
	 *
	 * @return o tipo do utilizador (NORMAL ou GESTOR)
	 */
	public TipoUtilizador getTipo() {
		return tipo;
	}
	
	/**
	 * Devolve a lista de anúncios publicados pelo utilizador.
	 *
	 * @return a lista de anúncios do utilizador
	 */
	public List<Anuncio> getAnuncios() {
		return anuncios;
	}
	
	/**
	 * Devolve a lista de chats em que o utilizador participa.
	 *
	 * @return a lista de chats do utilizador
	 */
	public List<Chat> getChats() {
		return chats;
	}
	 
	/**
	 * Altera o nome do utilizador.
	 *
	 * @param nome o novo nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * Altera o email do utilizador.
	 *
	 * @param email o novo email (deve ser único)
	 */
	public void setEmail(String email) {
		 this.email = email;
	}

	/**
	 * Altera a password do utilizador.
	 *
	 * @param password a nova password
	 */
	public void setPassword(String password) {
		 this.password = password;
	}
	 
	/**
	 * Devolve uma representação em texto do utilizador, com nome e email.
	 *
	 * @return o texto com o nome e o email do utilizador
	 */
	@Override
	public String toString() {
		return "Nome: " + nome + " E-mail: " + email ;
	}
	
}
