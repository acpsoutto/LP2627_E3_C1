package upt.lp.objetosperdidos;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name= "utilizador")

//cada utilizador tem: id, nome, email, password(chave) e tipo.
public class Utilizador {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idUtilizador")
	private int idUtilizador;
	
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
	
	@ManyToMany(mappedBy = "utilizadores")
	private List<Chat> chats = new ArrayList<>();
			
	//O Hibernate exige o construtor vazio
	public Utilizador() {
	}

	public Utilizador( String nome, String email, String password, TipoUtilizador tipo) {
		
		this.nome = nome;
		this.email = email;
		this.password = password;
		this.tipo = tipo;
	}

	public int getIdUtilizador() {
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
	
	 public List<Chat> getChats() {
	        return chats;
	   }
	 

	public void setNome(String nome) {
		this.nome = nome;
	}

	 public void setEmail(String email) {
		 this.email = email;
	 }

	 public void setPassword(String password) {
		 this.password = password;
	 }

	@Override
	public String toString() {
		return "Utilizador [idUtilizador=" + idUtilizador + ", nome=" + nome + ", email=" + email + ", password="
				+ password + ", tipo=" + tipo + "]";
	}
	
}
