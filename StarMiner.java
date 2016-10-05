import java.io.*;
import java.util.*;
import java.lang.Math;

public class StarMiner {
    public static final String DatabaseLocation = "C:\\HYG-Database-master\\hygxyz.csv";
    public static ArrayList<ArrayList<Double>> Stars;
        
    public static void main(String[] args) {
        Stars = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> starStats;
        Double searchRadius = Double.POSITIVE_INFINITY;
        
        System.out.println("Reading HYG formatted CSV file named \"hygxyz.csv\"");
        System.out.println("Found " + getStarData(searchRadius) + 
                           " stars within a radius of " + searchRadius + 
                           " parsecs from Sol.");
        starStats = getStarStats();
        System.out.println("minimum = " + starStats.get(0));
        System.out.println("maximum = " + starStats.get(1));
        System.out.println("mean    = " + starStats.get(2));
    }

    public static int getStarData(Double radius) {
        int numStars = 0;
        try {
            BufferedReader csv = new BufferedReader(new FileReader(DatabaseLocation));
            csv.readLine(); // skip first line
            for (String line = csv.readLine(); line != null; line = csv.readLine()) {
                String[] currentStarData = line.split(",");

                Boolean isDistanceAccurate = 
                        Double.parseDouble(currentStarData[9]) != 10000000;
                Boolean isWithinRadius = Double.parseDouble(currentStarData[9]) <= radius;
                if (isDistanceAccurate && isWithinRadius) {
                    ArrayList<Double> currentStar = new ArrayList<Double>();
                    for (int j = 17; j <= 19; j++)
                        currentStar.add(Double.parseDouble(currentStarData[j]));   
                    Stars.add(currentStar);
                    numStars++;
                }
            }
        } catch (IOException e) { /* e.printStackTrace() */ }
        return numStars;
    }
    
    public static ArrayList<Double> getStarStats() {
        final int MIN = 0, MAX = 1, AVG = 2;
        
        ArrayList<Double> stats = new ArrayList<Double>();
        stats.add(Double.POSITIVE_INFINITY); // create placeholder minimum
        stats.add(0.0); // create placeholder maximum
        stats.add(0.0); // create placeholder mean
                
        for (int i = 0; i < Stars.size(); i++) {
            Double nearestNeighbor = Double.POSITIVE_INFINITY;
            
            for (int j = 0; j < Stars.size(); j++) {
                if (j != i) {
                    Double currentDistance = computeDistance(i, j);
                    if (currentDistance < nearestNeighbor)
                        nearestNeighbor = currentDistance;
                }
            }
            
            if (nearestNeighbor < stats.get(MIN))
                stats.set(MIN, nearestNeighbor);
            
            if (nearestNeighbor > stats.get(MAX))
                stats.set(MAX, nearestNeighbor);
            
            Double currentAvg;
            if (i == 0)
                currentAvg = nearestNeighbor;
            else
                currentAvg = ((stats.get(AVG) * ((double) i)) + 
                             nearestNeighbor) / ((double) (i + 1));
            
            stats.set(AVG, currentAvg);
        }
        
        return stats;
    }
    
    public static Double computeDistance(int idx0, int idx1) {
        Double[][] starDims = new Double[2][3];
        
        int idx = idx0;
        for (int i = 0; i < 2; i++) {
            if (i == 1)
                idx = idx1;
            for (int j = 0; j < 3; j++)
                starDims[i][j] = Stars.get(idx).get(j);
        }
        
        Double distanceSquared = Math.pow(starDims[1][0] - starDims[0][0], 2.0) +
                                 Math.pow(starDims[1][1] - starDims[0][1], 2.0) +
                                 Math.pow(starDims[1][2] - starDims[0][2], 2.0);
        
        return Math.sqrt(distanceSquared);
    }
}