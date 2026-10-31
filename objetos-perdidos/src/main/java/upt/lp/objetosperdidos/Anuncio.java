package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

/**
 * Classe base abstrata dos anúncios da aplicação.
 * <p>
 * Guarda os dados comuns a todos os anúncios: título, publicador, categoria, estado,
 * descrição, cor e datas de publicação e de resolução. As subclasses acrescentam
 * os dados específicos de cada tipo:
 * <ul>
 *   <li>{@link AnuncioPerdido}: objeto perdido ("Procura-se"), com a data da perda;</li>
 *   <li>{@link AnuncioEncontrado}: objeto encontrado, com a localização.</li>
 * </ul>
 * Um anúncio pode ter vários {@link Chat}s, um por cada utilizador que o reivindique.
 * <p>
 * Esta classe é uma entidade JPA/Hibernate, mapeada na tabela {@code anuncio}
 * (herança do tipo JOINED: cada subclasse tem a sua própria tabela, ligada a esta).
 */
@Entity
@Table(name = "anuncio")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Anuncio {
	
	/** Identificador único do anúncio (gerado automaticamente pela base de dados). */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAnuncio")
	private int idAnuncio;
	
	/** Título do anúncio. Obrigatório. */
	@Column(nullable = false, length = 100)
	private String titulo;
	
	/** Utilizador que publicou o anúncio. Obrigatório. */
	@ManyToOne
	@JoinColumn(name = "idPublicador", nullable = false)
	private Utilizador utilizador;
	
	/** Categoria do objeto. Guardada na base de dados como texto. */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Categoria categoria;
	
	/** Estado do anúncio (PENDENTE ou RESOLVIDO). Guardado na base de dados como texto. */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Estado estado;
	
	/** Descrição do objeto. Obrigatória, com um máximo de 500 caracteres. */
	@Column(nullable = false, length = 500)
	private String descricao;
	
	/** Data em que o anúncio foi publicado. Obrigatória. */
	@Column(nullable = false)
	private LocalDate dataPublicacao;
	
	/** Cor do objeto. Obrigatória. */
	@Column(nullable = false, length = 30)
	private String cor;
	
	/** Data em que o anúncio foi resolvido. Fica vazia enquanto o anúncio estiver pendente. */
	@Column
	private LocalDate dataResolucao;
	
	/** Chats iniciados a partir deste anúncio. */
	@OneToMany(mappedBy = "anuncio", cascade = CascadeType.ALL)
	private List <Chat> chats = new ArrayList<>();
	
	/**
	 * Construtor vazio, necessário para o Hibernate criar instâncias da entidade.
	 */
	public Anuncio() {
	}
	
	/**
	 * Construtor: Cria um novo anúncio.
	 * O estado inicial é {@link Estado#PENDENTE} e a data de publicação é a data atual.
	 *
	 * @param utilizador utilizador que publica o anúncio
	 * @param titulo     título do anúncio
	 * @param categoria  categoria do objeto
	 * @param descricao  descrição do objeto
	 * @param cor        cor do objeto
	 */
	public Anuncio(Utilizador utilizador, String titulo, Categoria categoria, String descricao, String cor) {
        this.utilizador = utilizador;
        this.titulo = titulo;
        this.categoria = categoria;
        this.estado = Estado.PENDENTE;
        this.descricao = descricao;
        this.dataPublicacao = LocalDate.now();
        this.cor = cor;
    }
	
	/**
	 * Adiciona um chat a este anúncio e associa o anúncio ao chat,
	 * mantendo os dois lados da relação coerentes.
	 *
	 * @param chat o chat a adicionar
	 */
	public void addChat (Chat chat) {
		chats.add(chat);
		chat.setAnuncio(this);
	}
	
	/**
	 * Devolve o identificador do anúncio.
	 *
	 * @return o id do anúncio
	 */
	public int getIdAnuncio() {
		return idAnuncio;
	}

	/**
	 * Devolve o título do anúncio.
	 *
	 * @return o título
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Altera o título do anúncio.
	 *
	 * @param titulo o novo título
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Devolve o utilizador que publicou o anúncio.
	 *
	 * @return o utilizador publicador
	 */
	public Utilizador getUtilizador() {
		return utilizador;
	}
	
	/**
	 * Altera o utilizador que publicou o anúncio.
	 *
	 * @param utilizador o novo utilizador publicador
	 */
	public void setUtilizador(Utilizador utilizador) {
		this.utilizador = utilizador;
	}

	/**
	 * Devolve a categoria do objeto.
	 *
	 * @return a categoria
	 */
	public Categoria getCategoria() {
		return categoria;
	}

	/**
	 * Altera a categoria do objeto.
	 *
	 * @param categoria a nova categoria
	 */
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	/**
	 * Devolve o estado do anúncio.
	 *
	 * @return o estado (PENDENTE ou RESOLVIDO)
	 */
	public Estado getEstado() {
		return estado;
	}

	/**
	 * Altera o estado do anúncio.
	 *
	 * @param estado o novo estado
	 */
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	/**
	 * Devolve a descrição do objeto.
	 *
	 * @return a descrição
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * Altera a descrição do objeto.
	 *
	 * @param descricao a nova descrição (máximo de 500 caracteres)
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * Devolve a data em que o anúncio foi publicado.
	 *
	 * @return a data de publicação
	 */
	public LocalDate getDataPublicacao() {
		return dataPublicacao;
	}

	/**
	 * Altera a data de publicação do anúncio.
	 *
	 * @param dataPublicacao a nova data de publicação
	 */
	public void setDataPublicacao(LocalDate dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	/**
	 * Devolve a cor do objeto.
	 *
	 * @return a cor
	 */
	public String getCor() {
		return cor;
	}

	/**
	 * Altera a cor do objeto.
	 *
	 * @param cor a nova cor
	 */
	public void setCor(String cor) {
		this.cor = cor;
	}

	/**
	 * Devolve a data em que o anúncio foi resolvido.
	 *
	 * @return a data de resolução, ou {@code null} se o anúncio ainda estiver pendente
	 */
	public LocalDate getDataResolucao() {
		return dataResolucao;
	}

	/**
	 * Altera a data de resolução do anúncio.
	 *
	 * @param dataResolucao a nova data de resolução
	 */
	public void setDataResolucao(LocalDate dataResolucao) {
		this.dataResolucao = dataResolucao;
	}

	/**
	 * Devolve a lista de chats iniciados a partir deste anúncio.
	 *
	 * @return a lista de chats do anúncio
	 */
	public List<Chat> getChats() {
		return chats;
	}
	
}
