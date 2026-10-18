package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "anuncio")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Anuncio {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAnuncio")
	private long idAnuncio;
	
	@Column(nullable = false)
	private String titulo;
	
	@ManyToOne
	@JoinColumn(name = "idPublicador", nullable = false)
	private Utilizador utilizador;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Categoria categoria;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Estado estado;
	
	@Column(nullable = false, length = 500)
	private String descricao;
	
	@Column(nullable = false)
	private LocalDate dataPublicacao;
	
	@Column(nullable = false)
	private String cor;
	
	@Column
	private LocalDate dataResolucao;
	
	@OneToMany(mappedBy = "anuncio", cascade = CascadeType.ALL)
	private List <Chat> chats = new ArrayList<>();
	
	public Anuncio() {
	}
	
	public Anuncio(Utilizador utilizador, String titulo, Categoria categoria, String descricao, String cor) {
        this.utilizador = utilizador;
        this.titulo = titulo;
        this.categoria = categoria;
        this.estado = Estado.PENDENTE;
        this.descricao = descricao;
        this.dataPublicacao = LocalDate.now();
        this.cor = cor;
    }
	
	public void addChat (Chat chat) {
		chats.add(chat);
		chat.setAnuncio(this);
	}
	
	
	public long getIdAnuncio() {
		return idAnuncio;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Utilizador getUtilizador() {
		return utilizador;
	}

	public void setUtilizador(Utilizador utilizador) {
		this.utilizador = utilizador;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDate getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(LocalDate dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public LocalDate getDataResolucao() {
		return dataResolucao;
	}

	public void setDataResolucao(LocalDate dataResolucao) {
		this.dataResolucao = dataResolucao;
	}

	public List<Chat> getChats() {
		return chats;
	}
	
}
