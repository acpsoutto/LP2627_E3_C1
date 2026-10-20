package upt.lp.objetosperdidos;

/**
 * Tipos de utilizador existentes na aplicação.
 * <p>
 * O tipo define o menu a que o utilizador tem acesso depois de fazer login.
 * É guardado na base de dados como texto.
 */
public enum TipoUtilizador {
	/** Utilizador normal: pode criar anúncios, reivindicar objetos e usar os chats. */
	NORMAL,
	/** Gestor: tem acesso ao menu de gestão, onde pode ver as estatísticas. */
	GESTOR
}
