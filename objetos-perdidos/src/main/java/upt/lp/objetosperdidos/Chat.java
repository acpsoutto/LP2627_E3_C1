package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "chat")
public class Chat {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idChat")
	private long idChat;
	
	@ManyToOne
	@JoinColumn(name = "idAnuncio", nullable = false)
	private Anuncio anuncio;
	
	@Column(nullable = false)
	private LocalDate dataCriacao;
	
	@OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Mensagem>mensagens = new ArrayList<>();
	
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "chat_utilizador",
		joinColumns = @JoinColumn(name = "idChat"),
		inverseJoinColumns = @JoinColumn(name = "idUtilizador")
	)
	private List<Utilizador> utilizadores = new ArrayList<>();
	
	
	public Chat() {
	}

	public Chat(Anuncio anuncio, Utilizador interessado) {
		this.anuncio = anuncio;
		this.dataCriacao = LocalDate.now();
		
		Utilizador dono = anuncio.getUtilizador();
		addUtilizador(dono);
		addUtilizador(interessado);
	}
	
	public void addUtilizador(Utilizador u) {
	        if (utilizadores.size() < 2 && !utilizadores.contains(u)) {
	            utilizadores.add(u);
	        }
	        if (!u.getChats().contains(this)) {
	            u.getChats().add(this);
	        }
	}
	
	public boolean pertenceAoChat(Utilizador u) {
        for (Utilizador participante : utilizadores) {
            if (participante.getIdUtilizador() == u.getIdUtilizador()) {
                return true;
            }
        }
        return false;
    }
	
	public void adicionarMensagem(Mensagem m) {
		mensagens.add(m);
		m.setChat(this);
		
		if ((m.getEmissor().getIdUtilizador() == anuncio.getUtilizador().getIdUtilizador()) && (m.getTexto().equalsIgnoreCase("Este anuncio ficou resolvido"))) {
            anuncio.setEstado(Estado.RESOLVIDO);
            anuncio.setDataResolucao(LocalDate.now());
		}	
	}
	
	public Utilizador getCriador() {
		return anuncio.getUtilizador();
	}
	
	public Utilizador getInteressado() {
		for (Utilizador u : utilizadores) {
			if(u.getIdUtilizador() != anuncio.getUtilizador().getIdUtilizador()) {
				return u;
			}
		} 
		return null;
	}
	

	public long getIdChat() {
		return idChat;
	}

	public Anuncio getAnuncio() {
		return anuncio;
	}
	
	 public void setAnuncio(Anuncio anuncio) {
	        this.anuncio = anuncio;
	    }

	public LocalDate getDataCriacao() {
		return dataCriacao;
	}

	public List<Mensagem> getMensagens() {
		return mensagens;
	}

	public List<Utilizador> getUtilizador() {
		return utilizadores;
	}

	@Override
	public String toString() {
		return ""+idChat+" - "+ anuncio + " conversa entre " + utilizadores;
	}
}
