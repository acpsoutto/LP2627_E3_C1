package upt.lp.objetosperdidos;

import java.time.LocalDateTime;

public class Mensagem {
	
	private long idMensagem;
	private Chat chat;
	private Utilizador emissor;
	private LocalDateTime hora;
	private String texto;
	
	
	public Mensagem() {
		
	}


	public Mensagem(Chat chat, Utilizador emissor, String texto) {
		this.chat = chat;
		this.emissor = emissor;
		this.hora = LocalDateTime.now();
		this.texto = texto;
	}


	public long getIdMensagem() {
		return idMensagem;
	}

	public Chat getChat() {
		return chat;
	}


	public void setChat(Chat chat) {
		this.chat = chat;
	}


	public Utilizador getEmissor() {
		return emissor;
	}


	public void setEmissor(Utilizador emissor) {
		this.emissor = emissor;
	}


	public LocalDateTime getHora() {
		return hora;
	}


	public String getTexto() {
		return texto;
	}


	public void setTexto(String texto) {
		this.texto = texto;
	}


	@Override
	public String toString() {
		return ""+ emissor + ": "+ texto +" " + hora;
	}
	
	
	
	
}
