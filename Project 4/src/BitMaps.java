import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class BitMaps {
    public static  void main(String args[]) throws IOException {

    Scanner s=new Scanner(System.in);
    System.out.println("Enter the Single-Flow Spread Sketch type : ");
    String sketchType=s.next();
    int bitMapSize;
    int[] spreads;
    switch(sketchType.toUpperCase().trim())
    {
        case "BITMAP":
            System.out.println("Enter the bits in the BitMap ");
            bitMapSize =s.nextInt();
            spreads=new int[]{100,1000,10000,100000,1000000};
            generateBitMap(bitMapSize,spreads);
            break;
        case "PROBABILISTIC_BITMAP":
            System.out.println("Enter the bits in the BitMap ");
            bitMapSize =s.nextInt();
            spreads=new int[]{100,1000,10000,100000,1000000};
            System.out.println("Enter the sampling probability : ");
            double probability=s.nextDouble();
            generateProbabiltyBitMap(bitMapSize,spreads,probability);
            break;
        case "HYPERLOGLOG":
            System.out.println("Enter the Number of registers used in HLL :");
            int noofRegisters=s.nextInt();
            spreads=new int[]{1000,10000,100000,1000000};
            System.out.println("Enter the number of bits in each register : ");
            int noofBits=s.nextInt();
            generateHyperLogLog(noofRegisters,noofBits,spreads);

            break;
    }
}

    private static void generateHyperLogLog(int noofRegisters, int noofBits, int[] spreads) {
        int[]bitMapArray;
        double alpha=0.7213/(1+(1.079/(double)noofRegisters));
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<spreads.length;i++) {
            bitMapArray=new int[noofRegisters];
            long[] elements = generateLongInputs(spreads[i]);
            for(int j=0;j<elements.length;j++)
            {
                long hashValue = Long.valueOf(elements[j]).hashCode();
                int zeros=Long.numberOfLeadingZeros(hashValue%(long)Math.pow(2,32))%32;
                hashValue = Math.abs(hashValue);
                bitMapArray[(int)hashValue%noofRegisters]= Math.max(bitMapArray[(int)hashValue%noofRegisters],zeros+1);
            }
            double estimatedFlowSpread=getEstimatedFlowSpreadHyperLogLog(bitMapArray,alpha);
            sb.append("True spread = "+spreads[i]+" Estimated spread = "+estimatedFlowSpread+"\n");



        }
        generateOutPutFiles("hyperLogLog.txt",sb);
    }



    private static double getEstimatedFlowSpreadHyperLogLog(int[] bitMapArray,double alpha) {
        double sum=0;
        for(int i=0;i<bitMapArray.length;i++)
        {
            sum+=1.0/Math.pow(2,bitMapArray[i]);
        }
        return alpha*(bitMapArray.length*bitMapArray.length)*1/sum;
    }

    private static void generateProbabiltyBitMap(int bitMapSize, int[] spreads, double probability) {
        int a = (int) (Math.random() * bitMapSize) + 1;
        int W = generateWValue(a, bitMapSize);
        int[] bitMapArray;
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<spreads.length;i++) {
            int[] elements = generateInputs(spreads[i]);
            int x=getMaxValue(elements,a,W,bitMapSize);
            bitMapArray=new int[bitMapSize];
            for(int j=0;j<elements.length;j++) {
                int hashValue = (int) Math.floor(((a * (elements[j])) % W) / ((double) W / bitMapSize));
                hashValue = Math.abs(hashValue);
                if(hashValue<x*probability)
                {
                    bitMapArray[hashValue]=1;
                }
            }
            double estimatedFlowSpread=getEstimatedFlowSpreadProbabilityBitMap(bitMapArray,probability);
            sb.append("True spread = "+spreads[i]+"\t\t Estimated spread = "+estimatedFlowSpread+"\n");
        }
        generateOutPutFiles("probabilistic_bitmap.txt",sb);
    }

    private static double getEstimatedFlowSpreadProbabilityBitMap(int[] bitMapArray,double probability) {
        int u=0;
        for(int i=0;i<bitMapArray.length;i++)
        {
            if(bitMapArray[i]==0)
            {
                u++;
            }
        }
        double v=(double)u/(bitMapArray.length);
        return -(bitMapArray.length/(probability))*Math.log(v);
    }

    private static int getMaxValue(int[] elements,int a,int W,int bitMapSize) {
        int maxValue=0;
        for(int i=0;i<elements.length;i++)
        {
            int hashValue = (int) Math.floor(((a * (elements[i])) % W) / ((double) W / bitMapSize));
            hashValue = Math.abs(hashValue);
            maxValue=Math.max(maxValue,hashValue);
        }
        return maxValue;
    }

    private static void generateBitMap(int bitMapSize, int[] spreads)
    {
        int a = (int) (Math.random() * bitMapSize) + 1;
        int W = generateWValue(a, bitMapSize);
        int[] bitMapArray;
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<spreads.length;i++)
        {
            int[] elements=generateInputs(spreads[i]);
            bitMapArray=new int[bitMapSize];
            for(int j=0;j<elements.length;j++)
            {
                int hashValue=(int)Math.floor(((a*(elements[j]))%W)/((double)W/bitMapSize));
                hashValue=Math.abs(hashValue);
                bitMapArray[hashValue]=1;
            }

            double estimatedFlowSpread=getEstimatedFlowSpreadBitMap(bitMapArray);
            sb.append("True spread = "+spreads[i]+"\t\t Estimated spread = "+estimatedFlowSpread+"\n");
        }
        generateOutPutFiles("bitMap.txt",sb);

    }
    private static int generateWValue(int a, int tableEntries)
    {
        int W=(int)(Math.random() * tableEntries) + 1;
        while(gcd(a,W)!=1)
        {
            W=(int)(Math.random() * tableEntries) + 1;
        }
        return W;
    }
    public static int gcd(int a,int W)
    {
        if (W == 0) {
            return a;
        }
        return gcd(W, a % W);
    }
    private static double getEstimatedFlowSpreadBitMap(int[] bitMapArray) {
        int u=0;
        for(int i=0;i<bitMapArray.length;i++)
        {
            if(bitMapArray[i]==0)
            {
                u++;
            }
        }
        double v=(double)u/(bitMapArray.length);
        return -(bitMapArray.length)*Math.log(v);

    }

    private static void generateOutPutFiles(String fileName,StringBuilder str) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(String.valueOf(str));
            bw.close();
        } catch (Exception ex) {
            System.out.println("Exception in file creation");
        }
    }
    private static int[] generateInputs(int size) {
        int[] s = new int[size];
        //Using hashset to generate the unique values
        HashSet<Integer> hs = new HashSet<>();
        int i = 0;
        while (i < size) {
            int temp = (int) (Math.random() * Integer.MAX_VALUE) + 1;
            if (!hs.contains(temp)) {
                s[i] = temp;
                i++;
            }
            hs.add(temp);
        }
        return s;
    }
    private static long[] generateLongInputs(int size) {
        long[] s = new long[size];
        //Using hashset to generate the unique values
        HashSet<Long> hs = new HashSet<>();
        int i = 0;
        while (i < size) {
            long temp = (long)(Math.random() * Long.MAX_VALUE) + 1;
            if (!hs.contains(temp)) {
                s[i] = temp;
                i++;
            }
            hs.add(temp);
        }
        return s;
    }
}
