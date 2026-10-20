package upt.lp.objetosperdidos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.*;

/**
 * Representa uma mensagem enviada num {@link Chat}.
 * <p>
 * Cada mensagem pertence a um chat, tem um emissor, um texto e a hora a que foi enviada.
 * <p>
 * Esta classe é uma entidade JPA/Hibernate, mapeada na tabela {@code mensagem}.
 */
@Entity
@Table(name = "mensagem")
public class Mensagem {
	
	/** Identificador único da mensagem (gerado automaticamente pela base de dados). */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idMensagem")
	private int idMensagem;
	
	/** Chat a que a mensagem pertence (coluna {@code idChat}). Obrigatório. */
	@ManyToOne
	@JoinColumn(name = "idChat", nullable = false)
	private Chat chat;
	
	/** Utilizador que enviou a mensagem (coluna {@code idEmissor}). Obrigatório. */
	@ManyToOne
	@JoinColumn(name = "idEmissor", nullable = false)
	private Utilizador emissor;
	
	/** Data e hora a que a mensagem foi enviada. Obrigatória. */
	@Column(nullable = false)
	private LocalDateTime hora;
	
	/** Texto da mensagem. Obrigatório, com um máximo de 500 caracteres. */
	@Column(nullable = false, length = 500)
	private String texto;
	
	/**
	 * Construtor vazio, necessário para o Hibernate criar instâncias da entidade.
	 */
	public Mensagem() {
		
	}

	/**
	 * Construtor: Cria uma nova mensagem.
	 * A hora de envio é definida automaticamente com a data e hora atuais.
	 *
	 * @param chat    chat a que a mensagem pertence
	 * @param emissor utilizador que envia a mensagem
	 * @param texto   texto da mensagem (máximo de 500 caracteres)
	 */
	public Mensagem(Chat chat, Utilizador emissor, String texto) {
		this.chat = chat;
		this.emissor = emissor;
		this.hora = LocalDateTime.now();
		this.texto = texto;
	}

	/**
	 * Devolve o identificador da mensagem.
	 *
	 * @return o id da mensagem
	 */
	public int getIdMensagem() {
		return idMensagem;
	}
	/**
	 * Devolve o chat a que a mensagem pertence.
	 *
	 * @return o chat da mensagem
	 */
	public Chat getChat() {
		return chat;
	}

	/**
	 * Altera o chat a que a mensagem pertence.
	 *
	 * @param chat o novo chat
	 */
	public void setChat(Chat chat) {
		this.chat = chat;
	}

	/**
	 * Devolve o utilizador que enviou a mensagem.
	 *
	 * @return o emissor da mensagem
	 */
	public Utilizador getEmissor() {
		return emissor;
	}

	/**
	 * Altera o utilizador que enviou a mensagem.
	 *
	 * @param emissor o novo emissor
	 */
	public void setEmissor(Utilizador emissor) {
		this.emissor = emissor;
	}

	/**
	 * Devolve a data e hora a que a mensagem foi enviada.
	 *
	 * @return a data e hora de envio
	 */
	public LocalDateTime getHora() {
		return hora;
	}

	/**
	 * Devolve o texto da mensagem.
	 *
	 * @return o texto da mensagem
	 */
	public String getTexto() {
		return texto;
	}

	/**
	 * Altera o texto da mensagem.
	 *
	 * @param texto o novo texto (máximo de 500 caracteres)
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}

	/**
	 * Devolve uma representação em texto da mensagem, no formato
	 * "emissor: texto data hora" (a data e hora com o padrão {@code yyyy-MM-dd HH:mm}).
	 *
	 * @return o texto que descreve a mensagem
	 */
	@Override
	public String toString() {
		DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    return "" + emissor + ": " + texto + " " + hora.format(formato);
	}	
}
