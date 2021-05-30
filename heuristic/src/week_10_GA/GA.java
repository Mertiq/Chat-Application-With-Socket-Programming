/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package week_10_GA;

/**
 *
 * @author zekikus
 */
public class GA {

    static final int POP_SIZE = 10; // Store the population size
    static final int MAX_GENERATION = 50; // Store the maximum generation count
    static final int CHROMOSOME_LENGTH = 11; // Store the chromosome length or number of gene count
    static final double MUTATION_RATE = 0.5;

    public static void main(String[] args) {

        Population currentPopulation = new Population(POP_SIZE, CHROMOSOME_LENGTH);
        currentPopulation.initializePopulation(); // Generate the initial population
        currentPopulation.printPopulation();

        int generation = 0;

        while (generation < MAX_GENERATION) {
            Population population = new Population(POP_SIZE, CHROMOSOME_LENGTH);
            int count = 0;
            while (count < POP_SIZE) {
                Individual[] individuals = Utils.createMatingPool(currentPopulation.individuals, POP_SIZE - 1);
                // Selection
                Individual individual1 = currentPopulation.selection(individuals, 5);
                Individual individual2 = currentPopulation.selection(individuals, 5);
                // Crossover operation to the selected pairs
                Individual childIndividual = currentPopulation.crossover(individual1, individual2);
                // Mutate this child
                bitwiseMutation(childIndividual);
                childIndividual.evaluate();

                // Set generated child to the new population
                population.individuals[count] = childIndividual;
                
                count++;
            }
            
            currentPopulation = population;
            generation++;
            
            System.out.println("\n GENERATION " + generation);
            currentPopulation.printPopulation();
        }

    }

    public static void bitwiseMutation(Individual child) {
        for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
            if (Math.random() > MUTATION_RATE) {
                child.chromosome[i] = child.chromosome[i] ^ 1;
            }
        }
    }

}
