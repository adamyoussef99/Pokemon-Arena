/*Adam Youssef
 *This class handles the main flow of the game by allowing the uer to choose their
 *pokemon and handling how the battle plays out
 */

import java.util.*;
import java.io.*;
import java.awt.*;

public class PokemonArena{
	public static ArrayList<Pokemon> allPoks; //list of all pokemon that is later divided into the user and enemy pokemon
	public static ArrayList<Pokemon> userPoks = new ArrayList<Pokemon>(); 
	public static ArrayList<Pokemon> cpuPoks;
	public static String turn;
	public static Pokemon user, enemy;
	
	//stores pokemon as pokemon objects in an arraylist by using the data file.
	public static ArrayList<Pokemon>loadPoks(){
		ArrayList<Pokemon>poks = new ArrayList<Pokemon>();
		try{
			Scanner in = new Scanner(new BufferedReader(new FileReader("pokemon.txt")));
			in.nextLine(); //removes 28 when loading from data file because it is not needed
			while(in.hasNextLine()){
				String pokemon = in.nextLine(); //takes each line from text file to use as parameter for each pokemon in the constructor 
				poks.add(new Pokemon(pokemon)); 
			}
			in.close();
	
		}
		catch(IOException ex){
			System.out.println("Bruh, pokemon.txt?");
		}
		return poks; //ArrayList of all Pokemon
	}
	
	//allows user to pick their 4 pokemon
	public static void choosePokemon(){
		Scanner kb = new Scanner(System.in);
		boolean validchoices = true;
		int[]pokChoseNum = new int[4]; //array of chosen pokemon spots to be removed after pokemon are selected so the removal works properly
		System.out.println("The pokemon you can choose from are: "); //prints all pokemon to be chosen from at the beginning of the game
		for (int i=0;i<allPoks.size();i++){
			System.out.print((i+1) + ") " + allPoks.get(i) + "\n");
		}
		String[]order = {"first","second","third","fourth"};
		for (int i=0;i<4;i++){
			System.out.println("Enter the number of your " + order[i] + " pokemon:");
			int chosenPok = kb.nextInt();
			pokChoseNum[i] = chosenPok-1;
			userPoks.add(allPoks.get(chosenPok-1));
		}
		Arrays.sort(pokChoseNum); //sorts in ascending order to make removal easier
		for(int i=0;i<3;i++){
			if(pokChoseNum[i]==pokChoseNum[i+1]){
			validchoices = false;
			}
		}
		if(validchoices==false){
			System.out.println("You chose a Pokemon more than once. Your team is invalid. Choose again!");
			userPoks = new ArrayList<Pokemon>();
			choosePokemon();
		}
		else{
			cpuPoks = allPoks; //enemy pokemon is whatever you don't choose
			for (int i=0;i<4;i++){
				cpuPoks.remove(cpuPoks.get(pokChoseNum[i]-i));
			}
		}
	}
	
	public static void retreat(){
		Scanner kb = new Scanner(System.in);
		System.out.println("Choose a pokemon to fight with: ");
		boolean retreatDone = false;
		for(int i=0;i<userPoks.size();i++){ //prints all the pokemon the user has available
			System.out.println((i+1) + ") " + userPoks.get(i).name);
		}
		if(retreatDone==false){
			int newPok = kb.nextInt();
			user = userPoks.get(newPok-1); //changes user pokemon to the pokemon the user selects
			retreatDone=true; //stops letting user input another number
		}
	}
	
	//allows user to battle with 1 of their 4 pokemon
	public static void chooseCurrentPokemon(){ //chooses user pokemon based on user input at the beginning of each battle
		Scanner kb = new Scanner(System.in);
		System.out.println("What pokemon would you like to battle with? \nYour options are: ");
		for (int i=0;i<userPoks.size();i++){
			System.out.println((i+1) + ") " + userPoks.get(i).name); 
		}
		int chosenAsFirst = kb.nextInt();
		user = userPoks.get(chosenAsFirst-1); //user pokemon is changed to the chosen pokemon
		System.out.println(user.name + ", I choose you!");
	}
	
	//randomly selects enemy pokemon to battle
	public static void chooseCurrentEnemy(){ //randomly selects a pokemon to fight against, happens at the beginning of each battle
		Scanner kb = new Scanner(System.in);
		Random rand = new Random();
		int enemyChoice = (rand.nextInt(cpuPoks.size())); //a random number that's max is the size of the enemy list is chosen
		enemy = cpuPoks.get(enemyChoice); //enemy pokemon is changed to the random spot
		System.out.println("You will be battling against " + enemy.name + "."); //tells user the enemy chosen
	}
	
	//randomly selects action for enemy based on availability of actions
	public static void enemyTurn(){ 
		Random rand = new Random();
		enemy.makeNewAvailableAttacks(); //redefines available attacks every turn based on available energy
		int choice = 0;
		if(enemy.availableAttacks.size()==0 || enemy.stunned==true){ //if enemy has no available attacks or is stunned, they must pass 
			System.out.println(enemy.name + " cannot attack! They have passed.");
		}
		else if(enemy.stunned == false && enemy.availableAttacks.size()>0){
			choice = (rand.nextInt(enemy.availableAttacks.size())); //randomly chooses from available attacks if any
			enemy.attackPokemon(choice,user);
		}
	}
	
	//allows user to select to attack, pass, or retreat
	public static void userTurn(){ 
		Scanner kb = new Scanner(System.in);
		int choice = 0;
		user.makeNewAvailableAttacks(); //redefines available attacks every turn based on available energy
		System.out.println("1) Pass \n2) Retreat");
		if(user.availableAttacks.size()==0){ 
			System.out.println(user.name + " doesn't have enough energy to attack.");
		}
		else{
			System.out.println("The attacks you can use are: ");
			for (int i=0;i<user.availableAttacks.size();i++){ //prints available attacks to help user not choose an unavailable option
				System.out.println((i+3) + ") " + user.availableAttacks.get(i).name);
			}
		}
		choice = kb.nextInt();
		if (choice == 1){ //pass
			System.out.println("You have passed your turn. ");
		}
		if (choice==2 && user.stunned == false){
			retreat(); 
		}
		if (choice == 1 && user.stunned == true || choice>=3 && user.stunned == true){ //automatically selects pass if user is stunned
			System.out.println(user.name + " has been stunned! They can't attack or retreat.");
			System.out.println("You have passed your turn because you can't attack or retreat.");
		}
		if(choice>=3 && choice<3+user.numAttacks && user.stunned == false){ //attack only if not stunned and in the available attacks
			user.attackPokemon(choice-3,enemy); //choice -3 finds spot in list of available attacks 
		}
	}
	
	//restores energy of both players at the end of every turn to continue battle
	public static void addEnergy(){ 
		for(int i=0;i<userPoks.size();i++){
			if(userPoks.get(i).energy<=40){ //if the energy is less than 40 or 40, then it will always add 10
				userPoks.get(i).energy+=10;
			}
			else{
				userPoks.get(i).energy=50; //this is for when the energy is above 40, instead of adding 9,8,7.... just make it 50
			}
		}
		if(enemy.energy<50){
			enemy.energy+=10; //makes sure energy doesn't exceed 50
		}
	}
	
	//adds health to the user's pokemon after a win, and removes enemy pokemon from list of enemy pokemon.
	public static void endBattle(){
		for(int i=0;i<userPoks.size();i++){
			userPoks.get(i).hp+=20;
			if(userPoks.get(i).hp>userPoks.get(i).maxhp){
				userPoks.get(i).hp=userPoks.get(i).maxhp; //makes sure hp doesn't exceed max hp
			}
		}
		System.out.println(enemy.name + " has fainted!"); //battles end when enemy is defeated, so the enemy is removed from the cpuPoks ArrayList
		cpuPoks.remove(enemy);
		if(cpuPoks.size()>0){
			System.out.println("Get ready for the next battle... \n");
		}
	}
	
	public static boolean flip(){ //returns true or false, 50/50 chance for each, used for various attacks with 50% chance of happening
		return Math.random()<0.5;
	}
	
	public static void main (String[]args){
		
		System.out.println("Welcome to Pokemon Arena!");
		allPoks = loadPoks();
		choosePokemon();
		while(cpuPoks.size()>0 && userPoks.size()>0){ //battle loop, breaks when one player loses all of their pokemon
			chooseCurrentEnemy();
			chooseCurrentPokemon(); //whenever battles are finished, a new enemy and user pokemon is selected
			boolean first = flip();
			while(enemy.hp>0){ //turn loop
				if(first==true){ //50% chance user goes first
					System.out.println("\nIt is " + user.name + "'s turn: ");
					userTurn();
					user.stunned = false; //being stunned only lasts for one turn
					if(enemy.hp<=0){ //if the enemy pokemon has fainted, the turn is over
						break;
					}
					System.out.println("It is " + enemy.name + "'s turn...");
					enemyTurn();
					enemy.stunned = false; //so at the end of the turn, both pokemon get reset to not stunned
					if(user.hp<=0){ //if the user pokemon has fainted, 
						System.out.println(user.name + " has fainted!");
						userPoks.remove(user); //remove it from the pokemon they have,
						if(userPoks.size()==0){
							System.out.println("You have lost, and you're not a Master Coder either (e.g. Natalie, Mr. Mckenzie). What a shame.");
							break;
						}
						if(userPoks.size()>0){
							chooseCurrentPokemon(); //then choose another one
						}
					}
					if(userPoks.size()==0){
						System.out.println("You have lost, and you're not a Master Coder either (e.g. Natalie, Mr. Mckenzie). What a shame.");
						break;
					}
				}
				else if(first==false){ //50% chance enemy goes first
					System.out.println("It is " + enemy.name + "'s turn...");
					enemyTurn();
					enemy.stunned = false; //so at the end of the turn, both pokemon get reset to not stunned
					if(user.hp<=0){
						System.out.println(user.name + " has fainted!");
						userPoks.remove(user);
						if(userPoks.size()==0){
							System.out.println("You have lost. You are a disgrace, and have dishonoured your family. Goodbye.");
							break;
						}
						if(userPoks.size()>0){
							chooseCurrentPokemon(); //then choose another one
						}
					}
					System.out.println("\nIt is " + user.name + "'s turn: ");
					userTurn();
					user.stunned = false; //being stunned only lasts for one turn
					
				}
				addEnergy(); //adds energy to user and enemy pokemon at the end of the turn
			}
			if(userPoks.size()>0){ //doesn't end the battle when the user loses
				endBattle(); //ends then starts another battle
			}
			if(cpuPoks.size()==0){ //enemy loses when the last battle ends
				System.out.println("You have officially been crowned Trainer Supreme! You have brought great honour to your family. Goodbye.");
			}
		}	
	}
}