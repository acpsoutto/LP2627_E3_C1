package upt.lp.objetosperdidos;

import java.time.LocalDate;
import java.util.Scanner;

public class Teste {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Gerir gr = new Gerir();
		String email;
		String pass;
		int opL= 1;
		Categoria categoria;
		
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
			
			System.out.println("Email: ");
			email = sc.nextLine();
			
			if(opL == 1) {
				// VERIFICAR EMAIL NO SISTEMA
			}
			System.out.println("Palavra-Passe: ");
			pass = sc.nextLine();
			
			if(opL == 1) {
				//VERIFICAR PALAVRA PASSE CONFORME AS NORMAS REPTEIR ATÉ NORMAS CERTAS
				
				//ADICIONAR UTILIZADOR
			}
			else {
				//VERIFICAR PALAVRA-PASSE CORRETA 3/5 TENTATIVAS
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
				System.out.println("0- Logout.");
				System.out.println("Opção: ");
					
				op = sc.nextInt();
				sc.nextLine();
					
				switch(op) {
				case 1:
				case 2:
						
					gr.imprimirAnuncios(gr.filtrarAnuncios(op,null,null,null));
					int opt ;
					
					while(opt == 2) {
					
					System.out.println("1- Reivindicar Anuncio");
					System.out.println("2- Filtrar Pesquisa");
					System.out.println("Outra opção- Voltar");
					System.out.println("Opção");
					opt = sc.nextInt();
					sc.nextLine();
					
					if(opt == 1) {
						System.out.println("Qual anuncio quer reivindicar:");
						int Rid = sc.nextInt();
						sc.nextLine();
						gr.reivindicar(Rid,utiID);
					}
					if(opt == 2){
						System.out.println("Categoria (Enter para não inserir):");
						System.out.println("Eletronicos, Livros, Material_Escrita, Roupa");

						String cat = sc.nextLine().trim();
						
						System.out.println("Cor (Enter para não inserir):");
						String cor = sc.nextLine().trim();

						if (!cat.isEmpty()) {
						    categoria = Categoria.valueOf(cat.toUpperCase());
						}
						
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
}
