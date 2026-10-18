package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Chat {
	
	private long idChat;
	private Anuncio anuncio;
	private LocalDate dataCriacao;
	private List<Mensagem>mensagens = new ArrayList<>();
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
		return ""+idChat+"-"+ anuncio + " conversa entre " + utilizadores;
	}
}
