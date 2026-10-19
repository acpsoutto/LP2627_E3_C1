package upt.lp.objetosperdidos;

import jakarta.persistence.*;

/**
 * Anúncio de um objeto encontrado.
 * <p>
 * É publicado por quem encontrou o objeto e acrescenta ao {@link Anuncio}
 * o local onde o objeto foi encontrado.
 * <p>
 * Esta classe é uma entidade JPA/Hibernate, mapeada na tabela {@code anuncio_encontrado}
 * (herança do tipo JOINED: os dados comuns ficam na tabela {@code anuncio}).
 */
@Entity
@Table(name = "anuncio_encontrado")
public class AnuncioEncontrado extends Anuncio {
	
	/** Local onde o objeto foi encontrado. Obrigatório. */
	@Column(nullable = false)
	private String localizacao;

	/**
	 * Construtor vazio, necessário para o Hibernate criar instâncias da entidade.
	 */
	public AnuncioEncontrado() {
		
	}

	/**
	 * Construtor: Cria um novo anúncio de objeto encontrado.
	 * O estado inicial é {@link Estado#PENDENTE}
	 *
	 * @param utilizador  utilizador que publica o anúncio
	 * @param titulo      título do anúncio
	 * @param categoria   categoria do objeto
	 * @param descricao   descrição do objeto
	 * @param cor         cor do objeto
	 * @param localizacao local onde o objeto foi encontrado
	 */
	public AnuncioEncontrado(Utilizador utilizador, String titulo, Categoria categoria, String descricao, String cor, String localizacao) {
		super(utilizador, titulo, categoria, descricao, cor);
		this.localizacao = localizacao;
	}

	/**
	 * Devolve o local onde o objeto foi encontrado.
	 *
	 * @return a localização
	 */
	public String getLocalizacao() {
		return localizacao;
	}

	/**
	 * Altera o local onde o objeto foi encontrado.
	 *
	 * @param localizacao a nova localização
	 */
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	/**
	 * Devolve uma representação em texto do anúncio, com o id, título, cor,
	 * categoria, localização e utilizador que o publicou.
	 *
	 * @return o texto que descreve o anúncio
	 */
	@Override
	public String toString() {
		return ""+ getIdAnuncio() +"-Encontrou-se: " + getTitulo() +" "+ getCor() +" " +getCategoria() + " encontrado em " + localizacao + " por "+ getUtilizador();
	}
}
