package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Teste {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Gerir gr = new Gerir();
		gr.setup();
		int opL= 1;
		
		/**
		 * Login
		 */
		while(opL != 0) {
			System.out.println("Login:");
			System.out.println("1- Novo utilizador.");
			System.out.println("2- Utilizador existente.");
			System.out.println("0- Fechar programa");
			opL = sc.nextInt();
			sc.nextLine();
			
			if(opL == 0) {
				break;
			}
			if(opL < 1 || opL > 2 ) {
				System.out.println("Opção Invalida!");
				continue;
			}
			
			switch (opL) {
			case 1:
					System.out.print("Nome: ");
					String nome = sc.nextLine();

					System.out.print("Email: ");
					String email = sc.nextLine();

					if (gr.procurarPorEmail(email) != null) {
						System.out.println("Erro: Este email já está registado no sistema!");
						continue;
					}
					boolean registado = false;
					while (!registado) {
						System.out.print("Palavra-Passe (mínimo 1 maiúscula e 1 carácter especial): ");
						String pass = sc.nextLine();
						try {
							gr.registrar(nome, email, pass);
							System.out.println("Utilizador registado com sucesso! Faça agora o seu login.");
							registado = true;
						} catch (Exception e) {
							System.out.println("Erro: " + e.getMessage());
						}
					}
			continue;
			case 2:
				System.out.print("Email: ");
				email = sc.nextLine();

				boolean autenticado = false;
				for (int tentativa = 1; tentativa <= 3; tentativa++) {
					System.out.print("Palavra-Passe: ");
					String pass = sc.nextLine();
					try {
						gr.autenticar(email, pass);
						autenticado = true;
						System.out.println("Login efetuado com sucesso! Bem-vindo(a), " + gr.getPerfil().getNome());
						break;
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}

				if (!autenticado) {
					continue; // Se falhou as 3 tentativas, regressa ao menu de Login
				}
			break;
			}
			
			/**
			 * Menu de utilizador
			 */
			int op = 1;
			while(op != 0) {
				System.out.println("Menu");
				System.out.println("1- Mostrar anuncios de Procura-se.");
				System.out.println("2- Mostrar anunicos de Encontrados.");
				System.out.println("3- Criar anuncio.");
				System.out.println("4- Visualizar mensagens.");
				System.out.println("5- Ver e Atualizar os meus dados pessoais.");
				System.out.println("6- Eliminar anúncio");
				System.out.println("0- Logout.");
				System.out.println("Opção: ");
					
				op = sc.nextInt();
				sc.nextLine();
					
				switch(op) {
				case 1:
				case 2:
						
					gr.imprimirAnuncios(gr.filtrarAnuncios(op,null,null,null));
					int opt = 2;
					
					while(opt == 2) {
						System.out.println("1- Reivindicar Anuncio");
						System.out.println("2- Filtrar Pesquisa");
						System.out.println("Outra opção- Voltar");
						System.out.println("Opção");
						opt = sc.nextInt();
						sc.nextLine();
					
					if(opt == 1) {
						System.out.println("Qual anuncio quer reivindicar:");
						long Rid = sc.nextLong();
						sc.nextLine();
						gr.reivindicar(Rid, gr.getPerfil().getIdUtilizador());
					}
					if(opt == 2){
						Categoria categoriaFiltro = null;
						System.out.println("Categoria (Enter para não inserir):");
						System.out.println("Eletronicos, Livros, Material_Escrita, Roupa");

						String cat = sc.nextLine().trim();
						if (!cat.isEmpty()) {
							categoriaFiltro = lerCategoria(cat);
						}
						
						System.out.println("Cor (Enter para não inserir):");
						String cor = sc.nextLine().trim();
						
						 System.out.println("Texto no título (Enter para ignorar, mínimo 3 letras):");
				         String titulo = sc.nextLine().trim();
				            if (titulo.length() > 0 && titulo.length() < 3) {
				                System.out.println("Menos de 3 letras, o título foi ignorado.");
				                titulo = "";
				            }
				            gr.imprimirAnuncios(gr.filtrarAnuncios(op,categoria, cor,titulo));
						}
					}	
						
				
				break;
					
				case 3:
					System.out.println("Tipo de anúncio:");
				    System.out.println("1- Procura-se (Perdido).");
				    System.out.println("2- Encontrado.");
				    System.out.println("Opção: ");
				    int tipoAnuncio = sc.nextInt();
				    sc.nextLine();

				    System.out.println("Título: ");
				    String titulo = sc.nextLine();

				    System.out.println("Descrição: ");
				    String descricao = sc.nextLine();

				    System.out.println("Categoria (Eletronicos, Livros, Material_Escrita, Roupa): ");
				    String catStr = sc.nextLine().trim();
				    categoria = Categoria.valueOf(catStr.toUpperCase());

				    System.out.println("Cor: ");
				    String cor = sc.nextLine();

				    if (tipoAnuncio == 1) {
				        // Pedir data de perda (opcionalmente podes converter de String para LocalDate)
				        gr.criarAnuncioPerdido(titulo, categoria, descricao, cor, LocalDate.now());
				    } else if (tipoAnuncio == 2) {
				        System.out.println("Onde foi encontrado (Localização): ");
				        String localizacao = sc.nextLine();
				        gr.criarAnuncioEncontrado(titulo, categoria, descricao, cor, localizacao);
				    } else {
				        System.out.println("Tipo inválido!");
				    }
				    break;	
				    
				case 4:
					/**
					* LISTA DE CHATS E PODER ESCOLHER UM PARA CONTINUAR A FALAR;
					*/
				break;
					
				case 0:
					System.out.println("A fechar");
				break;
					
				default:
					System.out.println("Opção Invalida! Tente novamente!");
			}
		}	
	sc.close();
	}
	}
	private static Categoria lerCategoria(String texto) {
		for (Categoria c : Categoria.values()) {
			if (c.name().equalsIgnoreCase(texto.trim())) {
				return c;
			}
		}
		return null;
	}
}
