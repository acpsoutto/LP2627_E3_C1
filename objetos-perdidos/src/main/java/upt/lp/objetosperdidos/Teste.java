package upt.lp.objetosperdidos;

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
					
					System.out.println("1- Revendicar Anuncio");
					System.out.println("2- Filtrar Pesquisa");
					System.out.println("Outra opção- Voltar");
					System.out.println("Opção");
					opt = sc.nextInt();
					sc.nextLine();
					
					if(opt == 1) {
						System.out.println("Qual anuncio quer revendicar:");
						int Rid = sc.nextInt();
						sc.nextLine();
						gr.revindicar(Rid,utiID);
						
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
					System.out.println("Tipo de anunico:");
					System.out.println("1- Procura-se.");
					System.out.println("2- Encontrado");
					System.out.println("Opção: ");
						
					int opl = sc.nextInt();
					sc.nextLine();
						
					/**
					* Para ambos pedir:
					* Titulo, descrição, categoria,cor.
					*/
						
					if(op == 2 ) {
						System.out.println("Onde foi encontrado:");
						String localizacao = sc.nextLine();
					}
						
					/**
					* MOSTRAR POSSIVEIS ANUNICOS DO ARTIGO QUE ESTAMOS A TENTAR CRIAR (MATCH SYSTEM)
					* PERMITIR ESCOLHER ENTRE CRIAR OU REVEENDICAR ANUNCIO
					* 
					*/
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
