package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

/**
 * Representa uma conversa entre dois utilizadores sobre um {@link Anuncio}.
 * <p>
 * Um chat é criado quando um utilizador reivindica um anúncio. Os participantes são sempre dois:
 * o dono do anúncio e o utilizador interessado.
 * <p>
 * Esta classe é uma entidade JPA/Hibernate, mapeada na tabela {@code chat}.
 * Os participantes ficam guardados na tabela de ligação {@code chat_utilizador}.
 */

@Entity
@Table(name = "chat")
public class Chat {
	
	/** Identificador único do chat (gerado automaticamente pela base de dados). */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idChat")
	private long idChat;
	
	/** Anúncio a que o chat diz respeito (coluna {@code idAnuncio}). Obrigatório. */
	@ManyToOne
	@JoinColumn(name = "idAnuncio", nullable = false)
	private Anuncio anuncio;
	
	/** Data em que o chat foi criado. Obrigatória. */
	@Column(nullable = false)
	private LocalDate dataCriacao;
	
	/** Mensagens trocadas neste chat. */
	@OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Mensagem>mensagens = new ArrayList<>();
	
	/**
	 * Participantes do chat (no máximo dois), guardados na tabela {@code chat_utilizador}.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "chat_utilizador",
		joinColumns = @JoinColumn(name = "idChat"),
		inverseJoinColumns = @JoinColumn(name = "idUtilizador")
	)
	private List<Utilizador> utilizadores = new ArrayList<>();
	
	/**
	 * Construtor vazio, necessário para o Hibernate criar instâncias da entidade.
	 */
	public Chat() {
	}
	/**
	 * Construtor: Cria um novo chat sobre um anúncio.
	 * A data de criação é a data atual e os participantes são o dono do anúncio
	 * e o utilizador interessado.
	 *
	 * @param anuncio     anúncio a que o chat diz respeito
	 * @param interessado utilizador que reivindica o anúncio
	 */
	public Chat(Anuncio anuncio, Utilizador interessado) {
		this.anuncio = anuncio;
		this.dataCriacao = LocalDate.now();
		
		Utilizador dono = anuncio.getUtilizador();
		addUtilizador(dono);
		addUtilizador(interessado);
	}
	
	/**
	 * Adiciona um participante ao chat, mantendo os dois lados da relação coerentes.
	 * O utilizador só é adicionado se o chat ainda tiver menos de dois participantes
	 * e se o utilizador ainda não fizer parte dele.
	 *
	 * @param u o utilizador a adicionar
	 */
	public void addUtilizador(Utilizador u) {
	        if (utilizadores.size() < 2 && !utilizadores.contains(u)) {
	            utilizadores.add(u);
	        }
	        if (!u.getChats().contains(this)) {
	            u.getChats().add(this);
	        }
	}
	
	/**
	 * Verifica se um utilizador é participante deste chat.
	 *
	 * @param u o utilizador a verificar
	 * @return {@code true} se o utilizador fizer parte do chat, {@code false} caso contrário
	 */
	public boolean pertenceAoChat(Utilizador u) {
        for (Utilizador participante : utilizadores) {
            if (participante.getIdUtilizador() == u.getIdUtilizador()) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * Adiciona uma mensagem ao chat e associa a mensagem a este chat.
	 * <p>
	 * Se a mensagem for enviada pelo dono do anúncio e o texto for
	 * "Este anuncio ficou resolvido" (sem distinguir maiúsculas de minúsculas),
	 * o anúncio passa ao estado {@link Estado#RESOLVIDO} e a data de resolução
	 * fica com a data atual.
	 *
	 * @param m a mensagem a adicionar
	 */
	public void adicionarMensagem(Mensagem m) {
		mensagens.add(m);
		m.setChat(this);
		
		if ((m.getEmissor().getIdUtilizador() == anuncio.getUtilizador().getIdUtilizador()) && (m.getTexto().equalsIgnoreCase("Este anuncio ficou resolvido"))) {
            anuncio.setEstado(Estado.RESOLVIDO);
            anuncio.setDataResolucao(LocalDate.now());
		}	
	}
	
	/**
	 * Devolve o criador do chat, que é o dono do anúncio.
	 *
	 * @return o utilizador que publicou o anúncio
	 */
	public Utilizador getCriador() {
		return anuncio.getUtilizador();
	}
	
	/**
	 * Devolve o utilizador interessado, ou seja, o participante que não é o dono do anúncio.
	 *
	 * @return o utilizador interessado, ou {@code null} se não existir
	 */
	public Utilizador getInteressado() {
		for (Utilizador u : utilizadores) {
			if(u.getIdUtilizador() != anuncio.getUtilizador().getIdUtilizador()) {
				return u;
			}
		} 
		return null;
	}
	
	/**
	 * Devolve o identificador do chat.
	 *
	 * @return o id do chat
	 */
	public long getIdChat() {
		return idChat;
	}

	/**
	 * Devolve o anúncio a que o chat diz respeito.
	 *
	 * @return o anúncio do chat
	 */
	public Anuncio getAnuncio() {
		return anuncio;
	}
	
	/**
	 * Altera o anúncio a que o chat diz respeito.
	 *
	 * @param anuncio o novo anúncio
	 */
	 public void setAnuncio(Anuncio anuncio) {
	        this.anuncio = anuncio;
	    }

	/**
	 * Devolve a data em que o chat foi criado.
	 *
	 * @return a data de criação
	 */
	public LocalDate getDataCriacao() {
		return dataCriacao;
	}

	/**
	 * Devolve as mensagens trocadas neste chat.
	 *
	 * @return a lista de mensagens
	 */
	public List<Mensagem> getMensagens() {
		return mensagens;
	}

	/**
	 * Devolve os participantes do chat.
	 *
	 * @return a lista de utilizadores que participam no chat
	 */
	public List<Utilizador> getUtilizador() {
		return utilizadores;
	}

	/**
	 * Devolve uma representação em texto do chat, com o id, o anúncio
	 * e os participantes da conversa.
	 *
	 * @return o texto que descreve o chat
	 */
	@Override
	public String toString() {
		return ""+idChat+" - "+ anuncio + " conversa entre " + utilizadores;
	}
}
