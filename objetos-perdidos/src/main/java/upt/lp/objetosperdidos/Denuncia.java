package upt.lp.objetosperdidos;

import java.time.LocalDateTime;
import jakarta.persistence.*;



@Entity
@Table(name = "denuncia", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"idDenunciante", "idChat"}) // Evita que o mesmo utilizador denuncie o mesmo chat várias vezes
})

public class Denuncia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idDenuncia")
	private int idDenuncia;
	
	@ManyToOne
	@JoinColumn(name = "idDenunciante", nullable = false)
	private Utilizador denunciante;
	
	@ManyToOne
	@JoinColumn(name = "idDenunciado", nullable = false)
	private Utilizador denunciado;
	
	@ManyToOne
	@JoinColumn(name = "idChat", nullable = false)
	private Chat chat;
	
	@Column(nullable = false)
	private LocalDateTime dataHora;
	
	@Column(nullable = false)
	private String motivo;
	
	/** Estado do anúncio (PENDENTE ou RESOLVIDO). Guardado na base de dados como texto. */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private Estado estado;
	
	
	public Denuncia() {}
	
	public Denuncia(Utilizador denunciante, Utilizador denunciado, Chat chat, String motivo) {
		this.denunciado = denunciado;
		this.denunciante = denunciante;
		this.chat = chat;
		this.dataHora = LocalDateTime.now();
		this.motivo = motivo;
		this.estado = Estado.PENDENTE;
	}
	
	
	public int getIdDenuncia() {
		return idDenuncia;
	}
	
	/**
	 * @return the estado
	 */
	public Estado getEstado() {
		return estado;
	}

	/**
	 * @param estado the estado to set
	 */
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Utilizador getDenunciante() {
		return denunciante;
	}
	
	public Utilizador getDenunciado() {
		return denunciado;
	}
	
	public Chat getChat() {
		return chat;
	}
	
	public LocalDateTime getDataHora() {
		return dataHora;
	}
}