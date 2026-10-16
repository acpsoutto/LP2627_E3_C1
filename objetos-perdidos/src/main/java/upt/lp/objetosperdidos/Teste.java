package upt.lp.objetosperdidos;

import java.util.Scanner;

public class Teste {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String email;
		String pass;
		int opL= 1;
		
		/**
		 * Login
		 */
		while(opL != 0) {
			System.out.println("Login:");
			System.out.println("1- Novo utilizador.");
			System.out.println("2- Utilizador existem.");
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
				System.out.println("4- Visualizar menssagens.");
				System.out.println("0- Logout.");
				System.out.println("Opção: ");
					
				op = sc.nextInt();
				sc.nextLine();
					
				switch(op) {
				case 1, 2:
						
					if(op == 1) {
						//IMPRIMIR ANUNCIOS DE PROCURA-SE
					}
					else {
						//IMPRIMIR ANUNCIOS DE ENCONTRADOS
					}
						
					/*
					 * REENVIDICAR ANUNCIO
					 * PESQUISA FILTRADA
					 */
				
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
