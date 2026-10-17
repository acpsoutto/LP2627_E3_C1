package upt.lp.objetosperdidos.model;

//cada utilizador tem: id, nome, email, password(chave) e tipo.
public class Utilizador {
	private int idUtilizador;
	private String nome;
	private String email;
	private String password;
	private TipoUtilizador tipo;
	
	public Utilizador(int idUtilizador, String nome, String email, String password, TipoUtilizador tipo) {
		
		this.idUtilizador = idUtilizador;
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
	
	
	

}
