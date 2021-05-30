/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package week_10_GA;

import java.util.Arrays;

/**
 *
 * @author zekikus
 */

// Each individual has fitness value and chromosome array
public class Individual {
    
    public int fitness; // Store the fitness value of this individual, for this problem fitness value store the number of 1's count
    public int [] chromosome; // Store the each gene of the chromosome
    
    // 1 1 1 1 1 1 1 1 1 1 1 
    
    public Individual(int length, boolean init){
        chromosome = new int[length];
        if(init){
            // Fill chrosome array with random values between 0-1
            initializeChromosome();
            // Evaluate this individual
            evaluate();
        }
    }
    
    // Initialize the genes of the chrosome, individual
    public void initializeChromosome(){
        for (int i = 0; i < chromosome.length; i++) {
            if(Math.random() >= 0.5){
                chromosome[i] = 1;
            } else{
                chromosome[i] = 0;
            }
        }
    }
    
    // Calculate the fitness value of this chromosome, individual
    // In this problem, we calculate the number of 1's 
    public void evaluate(){
        for (int i = 0; i < chromosome.length; i++) {
            fitness += chromosome[i];
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(chromosome) + " - Fitness:" + fitness;
    }
    
    
    
}
