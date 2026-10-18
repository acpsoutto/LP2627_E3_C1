package upt.lp.objetosperdidos;

import jakarta.persistence.*;

@Entity
@Table(name= "utilizador")

//cada utilizador tem: id, nome, email, password(chave) e tipo.
public class Utilizador {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idUtilizador")
	private long idUtilizador;
	
	@Column (nullable = false)
	private String nome;
	
	@Column(nullable = false, unique =true)// não pode haver dois emails iguais
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	// guarda o texto "Normal" ou "Gestor" na BD (e não um número)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoUtilizador tipo;
	
	// Obrigatório para o Hibernate
	public Utilizador() {
	}

	// Sem id: é a base de dados que o gera
	public Utilizador(String nome, String email, String password, TipoUtilizador tipo) {
	    this.nome = nome;
	    this.email = email;
	    this.password = password;
	    this.tipo = tipo;
	}
		public long getIdUtilizador() {
		return idUtilizador;
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public TipoUtilizador getTipo() {
		return tipo;
	}

	@Override
	public String toString() {
		return "Utilizador [idUtilizador=" + idUtilizador + ", nome=" + nome + ", email=" + email
				+ ", tipo=" + tipo + "]";
	}
	
}
