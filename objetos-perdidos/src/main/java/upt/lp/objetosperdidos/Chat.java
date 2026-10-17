package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.ArrayList;

public class Chat {
	
	private int idChat;
	private Anuncio anuncio;
	private LocalDate dataCriacao;
	private ArrayList<Mensagem>mensagens;
	private Utilizador interessado;
	private Utilizador criador;
	
	public Chat() {
		
	}

	public Chat(Anuncio anuncio, Utilizador interessado) {
		this.anuncio = anuncio;
		this.interessado = interessado;
		this.dataCriacao = LocalDate.now();
		this.criador= anuncio.getUtilizador();
		mensagens = new ArrayList<>();
	}
	
	public void adicionarMensagem(Mensagem m) {
		mensagens.add(m);
	}

	public int getIdChat() {
		return idChat;
	}

	public void setIdChat(int idChat) {
		this.idChat = idChat;
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

	public void setDataCriacao(LocalDate dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public ArrayList<Mensagem> getMensagens() {
		return mensagens;
	}

	public void setMensagens(ArrayList<Mensagem> mensagens) {
		this.mensagens = mensagens;
	}

	public Utilizador getInteressado() {
		return interessado;
	}

	public void setInteressado(Utilizador interessado) {
		this.interessado = interessado;
	}

	public Utilizador getCriador() {
		return criador;
	}

	public void setCriador(Utilizador criador) {
		this.criador = criador;
	}

	@Override
	public String toString() {
		return "Chat [idChat=" + idChat + ", dataCriacao=" + dataCriacao + "]";
	}
	
	
	
	
	
	
	
}
