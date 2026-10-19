package upt.lp.objetosperdidos;
	
import java.time.LocalDate;
import jakarta.persistence.*;

/**
 * Anúncio de um objeto perdido ("Procura-se").
 * <p>
 * É publicado por quem perdeu o objeto e a data em que o objeto foi perdido.
 * <p>
 * Esta classe é uma entidade JPA/Hibernate, mapeada na tabela {@code anuncio_perdido}
 */
@Entity
@Table(name = "anuncio_perdido")
public class AnuncioPerdido extends Anuncio{
	
	/** Data em que o objeto foi perdido. Obrigatória. */
	@Column(nullable = false)
	private LocalDate dataPerda;

	/**
	 * Construtor vazio, necessário para o Hibernate criar instâncias da entidade.
	 */
	public AnuncioPerdido() {
	}

	/**
	 * Construtor:Cria um novo anúncio de objeto perdido.
	 * O estado inicial é {@link Estado#PENDENTE} e a data de publicação é a data atual
	 * (definidos no construtor de {@link Anuncio}).
	 *
	 * @param utilizador utilizador que publica o anúncio
	 * @param titulo     título do anúncio
	 * @param categoria  categoria do objeto
	 * @param descricao  descrição do objeto
	 * @param cor        cor do objeto
	 * @param dataPerda  data em que o objeto foi perdido
	 */
	public AnuncioPerdido(Utilizador utilizador, String titulo, Categoria categoria, String descricao, String cor, LocalDate dataPerda) {
		super(utilizador, titulo, categoria, descricao, cor);
		this.dataPerda = dataPerda;
	}

	/**
	 * Devolve a data em que o objeto foi perdido.
	 *
	 * @return a data da perda
	 */
	public LocalDate getDataPerda() {
		return dataPerda;
	}

	/**
	 * Altera a data em que o objeto foi perdido.
	 *
	 * @param dataPerda a nova data da perda
	 */
	public void setDataPerda(LocalDate dataPerda) {
		this.dataPerda = dataPerda;
	}

	/**
	 * Devolve uma representação em texto do anúncio, com o id, título, cor,
	 * categoria, data da perda e utilizador que o publicou.
	 *
	 * @return o texto que descreve o anúncio
	 */
	@Override
	public String toString() {
		return ""+ getIdAnuncio() +"-Procura-se: " + getTitulo() +" "+ getCor() +" " +getCategoria() + " perdido no " + dataPerda + " por "+ getUtilizador();
	}

}
