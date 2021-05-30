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
public class Utils {
    
    // You can use many selection operation to create the mating pool
    // 1) Get random n solution from the current population (without replacement)
    // 2) Select random k solution from the current population, k must be equal to the population size
    // If you use the second method possibly more than one copy of some individuals in the mating pool (within replacement)
    
    public static Individual[] createMatingPool(Individual[] population, int poolSize){
        
        // Creates the random numbers without replacement
        // Length of the random selected Indexes array will be equal to poolSize in this example
        int[] randIndexes = new Random().ints(0, population.length - 1).distinct().limit(poolSize).toArray();
        
        // Copy selected Individuals from the current population to the matingPool
        Individual[] matingPool = new Individual[poolSize];
        for (int i = 0; i < matingPool.length; i++) {
            matingPool[i] = population[randIndexes[i]];
        }
        
        return matingPool;
    }
    
}
