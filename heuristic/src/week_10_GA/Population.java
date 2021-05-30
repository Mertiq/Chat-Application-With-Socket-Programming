/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package week_10_GA;

import java.util.Random;

/**
 *
 * @author zekikus
 */
public class Population {

    private int popSize; // Store the population size
    private int chromosomeLength; // Store the length of the chrosome array
    public Individual[] individuals; // Store the each individual, chromosome in this population

    public Population(int popSize, int chromosomeLength) {
        this.popSize = popSize;
        this.chromosomeLength = chromosomeLength;
        individuals = new Individual[popSize];
    }

    // Initialize the population. Create random individuals and put this individual to the individuals array
    public void initializePopulation() {
        for (int i = 0; i < popSize; i++) {
            individuals[i] = new Individual(chromosomeLength, true);
        }
    }
    
    // Print each individual's chromosomes
    public void printPopulation(){
        for (int i = 0; i < individuals.length; i++) {
            System.out.println("Chrosome #" + i + ": " + individuals[i]);
        }
    }
    
    public Individual selection(Individual[] population, int k) {
        Individual bestIndividual = null;
        Random random = new Random();
        for(int i = 0; i < k; i++){
            Individual individual = population[random.nextInt(population.length)];
            
            if(bestIndividual == null){
                bestIndividual = individual;
            }
            
            if(individual.fitness > bestIndividual.fitness){
                bestIndividual = individual;
            }
        }
        return bestIndividual;
    }
    
    public Individual crossover(Individual individual1, Individual individual2) {
        Individual childIndividual = new Individual(chromosomeLength, false);
        
        for(int i = 0; i < childIndividual.chromosome.length; i++){
            
            if(Math.random() <= 0.5 ){
                childIndividual.chromosome[i] = individual2.chromosome[i];
            }
            else{
                childIndividual.chromosome[i] = individual1.chromosome[i];
            }
        }
        
        return childIndividual;
    }
    


}
