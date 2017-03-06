/*Adam Youssef
 *The Pokemon class is used to store pokemon objects that are to be used in PokemonArena.
 *
 */

import java.util.*;
import java.io.*;
import java.awt.*;

class Pokemon{
	public String name,type,resistance,weakness; //all objects for each pokemon
	public int hp,maxHp,numAttacks,energyCost,damage,energy,maxEnergy,disabled,choice;
	public boolean stunned;
	public ArrayList<Attack>attacks;
	public ArrayList<Attack>availableAttacks;
	
	public Pokemon(String stats){ //constructs pokemon objects
		String []items = stats.split(",");
		name = items[0];
		maxHp = Integer.parseInt(items[1]); 
		hp = Integer.parseInt(items[1]);
		type = items[2];
		resistance = items[3];
		weakness = items[4];
		numAttacks = Integer.parseInt(items[5]);
		attacks = new ArrayList<Attack>();
		for(int i = 0; i<numAttacks; i++){
			String[]attack = {items[6+i*4],items[7+i*4],items[8+i*4],items[9+i*4]}; //array for attack objects made to be used as parameter for attack class constructor method
			attacks.add(new Attack(attack)); //adds attacks to ArrayList to be found in various parts of the game
		}
		energy = 50; 
		disabled = 0; //value subtracted from damage, becomes 10 when disabled
		availableAttacks = new ArrayList<Attack>(); //list of attacks that can be used
		for(int i = 0; i<numAttacks; i++){
			if(energy>=attacks.get(i).energyCost){
				availableAttacks.add(attacks.get(i));
			}
		}
		stunned = false;
	}
	public void makeNewAvailableAttacks(){ //recreates the list of available attacks as energy is constantly changing
		availableAttacks = new ArrayList<Attack>();
		for(int i = 0; i<numAttacks; i++){
			if(energy>=attacks.get(i).energyCost){
				availableAttacks.add(attacks.get(i));
			}
		}
	}
	public void attackPokemon(int attackSpot, Pokemon enemy){
		Attack chosenAttack = availableAttacks.get(attackSpot);
		System.out.println(name + " used " + chosenAttack.name +"!");
		//energy cost
		energy-=chosenAttack.energyCost;
		int damage = Math.max(0,chosenAttack.damage-disabled); //if the pokemon being disabled makes the damage less than 0, 0 is taken as damage instead. If not it is just the damage minus disabled
		String special = chosenAttack.special;
		//weakness, doubles damage
		if (type.equals(enemy.weakness)){
			damage*=2;
		}	
		//resistance, divides damage by 2
		if (type.equals(enemy.resistance)){
			damage/=2;
		}
		if (special.equals(" ")){ //basic attack
			//deal damage
			enemy.hp-=damage;
			if(enemy.hp<0){
				enemy.hp=0;
			}
			System.out.println(enemy.name + "'s HP is now at " + (enemy.hp) + "."); 
		}
		
		//special
		if (special.equals("stun")){
			enemy.hp-=damage;
			if(enemy.hp<0){
				enemy.hp=0;
			}
			System.out.println(enemy.name + "'s HP is now at " + (enemy.hp) + ".");
		} //stun damage is done for sure, but 50% chance that it will stun, along with 50% for wild card and storm
		if(flip()==true){
			if (special.equals("stun")){ //stun
				enemy.stunned = true;
				System.out.println(enemy.name + " has been stunned.");
			}
			if (special.equals("wild card")){ //wild card
				enemy.hp-=damage;
				if(enemy.hp<0){ //used in all attacks, makes hp 0 if it is less than 0 so print statements are proper and pokemon are removed properly
					enemy.hp=0;
				}
				System.out.println("Wild card was successful.");
				System.out.println(enemy.name + "'s HP is now at " + (enemy.hp) + ".");
			}
			if (special.equals("wild storm")){ //wild storm
				while(true){
					if(flip()==true){
						enemy.hp-=damage;
						if(enemy.hp<0){
							enemy.hp=0;
						}
						System.out.println("Wild storm was successful.");
						System.out.println(enemy.name + "'s HP is now at " + (enemy.hp) + ".");
					}
					else{
						System.out.println("Wild storm was unsuccessful.");
						break; //keeps dealing damage until false is returned, then stops carrying out wild storm attack
					}
				}
			}
		}
		else{ //if flip returns false (50% of the time) specials don't work
			if (special.equals("stun")){ //stun
				System.out.println("The attempt to stun " + enemy.name + " failed!");
			}
			if (special.equals("wild card")){ //wild card
				System.out.println("Wild card wasn't successful.");
			}
			if (special.equals("wild storm")){ //wild storm
				System.out.println("Wild storm wasn't successful.");
			}
		}

		if (special.equals("disable")){ //disable
		enemy.disabled=10; // value used for damage subtraction when the pokemon gets disabled
			System.out.println(enemy.name + " was disabled! Their attacks will now deal 10 less damage.");
			enemy.hp-=damage;
			if(enemy.hp<0){
				enemy.hp=0;
			}
			System.out.println(enemy.name + "'s HP is now at " + (enemy.hp) + ".");
		}
		
		if (special.equals("recharge")){ //recharge
			if(energy<=30){ //makes sure energy doesn't exceed 50
				energy+=20;
			}
			else{
				energy=50; 
			}
			System.out.println(name + " has replenished 20 energy points! Their energy level is now: " + energy + ".");
		}
	}
	
	public boolean flip(){ //returns true or false, 50/50 chance for each, used for various attacks with 50% chance of happening
		return Math.random()<0.5;
	}
	
	
	public String toString(){
		return name;
	}
	
	class Attack{ //constructs attack objects
		String name, special; 
		int damage,energyCost;
		
		public Attack(String[]details){	//the array parameter is defined in the pokemon class	
			name = details[0];
			energyCost = Integer.parseInt(details[1]);
			damage = Integer.parseInt(details[2]);
			special = details[3];
		}
	}
} 