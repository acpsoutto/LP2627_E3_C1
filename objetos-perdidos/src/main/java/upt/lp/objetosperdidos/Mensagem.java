package upt.lp.objetosperdidos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.*;

@Entity
@Table(name = "mensagem")
public class Mensagem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idMensagem")
	private long idMensagem;
	
	@ManyToOne
	@JoinColumn(name = "idChat", nullable = false)
	private Chat chat;
	
	@ManyToOne
	@JoinColumn(name = "idEmissor", nullable = false)
	private Utilizador emissor;
	
	@Column(nullable = false)
	private LocalDateTime hora;
	
	@Column(nullable = false, length = 500)
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
		DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    return "" + emissor + ": " + texto + " " + hora.format(formato);
	}
	
	
	
	
}
