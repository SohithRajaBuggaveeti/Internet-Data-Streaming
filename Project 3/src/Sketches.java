import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
public class Sketches {
    public static  void main(String args[]) throws IOException {
        Scanner s=new Scanner(System.in);
        System.out.println("Enter the type of Sketching : ");
        String sketchType=s.next();
        int noOfCounterArrays;
        int counterSize;
        switch(sketchType.toUpperCase().trim())
        {
            case "COUNT_MIN":
                System.out.println("Enter the Number of counter Arrays : ");
                noOfCounterArrays =s.nextInt();
                System.out.println("Enter the Number of counters in each array : ");
                counterSize=s.nextInt();
                generateCountMin(noOfCounterArrays,counterSize);
                break;
            case "COUNTER_SKETCH":
                System.out.println("Enter the Number of  counter Arrays : ");
                noOfCounterArrays =s.nextInt();
                System.out.println("Enter the Number of counters in each array : ");
                counterSize=s.nextInt();
                generateCounterSketch(noOfCounterArrays,counterSize);
                break;
            case "ACTIVE_COUNTER":

                int bitCount=32;
                System.out.println("Enter the number of bits in the number part");
                int bitCountNumberPart=s.nextInt();
                System.out.println("Enter the number of bits in the exponent part");
                int bitCountExponentPart=s.nextInt();
                System.out.println("Enter the active counter size : ");
                int activeCounterSize=s.nextInt();
                generateActiveCounter(bitCount,bitCountNumberPart,bitCountExponentPart,activeCounterSize);

                break;
        }
    }

    private static void generateActiveCounter(int bitCount, int bitCountNumberPart, int bitCountExponentPart,int activeCounterSize) {
        int cn=0;
        int ce=0;
        for(int i=0;i<activeCounterSize;i++)
        {
            int arr[]=activeIncrease(i,cn,ce,bitCountNumberPart,bitCountExponentPart);
            cn=arr[0];
            ce=arr[1];
        }
        double result=(cn*Math.pow(2,ce));
        StringBuilder sb=new StringBuilder();
        sb.append("Active Counter : "+result);
        generateOutPutFiles("activeCounter.txt",sb);

        }

    private static int[] activeIncrease(int c, int cn, int ce,int bitCountNumberPart,int bitCountExponentPart) {
        int randomNumber=(int)(Math.random() * Integer.MAX_VALUE);
        int probability=randomNumber%((int)Math.pow(2,ce));
        if(probability==0)
        {
            cn++;
        }
        if(cn>((int)Math.pow(2,bitCountNumberPart)-1))
        {
            ce++;
            cn=cn>>1;
        }
        return new int[]{cn,ce};
    }

    private static void generateCounterSketch(int noOfCounterArrays, int counterSize) throws IOException {
        int[] s = generateRandomNumbers(noOfCounterArrays);
        File file = new File("project3input.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        int flowSize = Integer.parseInt(br.readLine());
        Flow f[]=new Flow[flowSize];
        String str;
        int i=0;
        while((str = br.readLine()) != null) {
            String[] st = str.split("\\s+");
            f[i++] = new Flow(st[0],st[1]);
        }
        int counterMinArray[][]=new int[noOfCounterArrays][counterSize];
        for(i=0;i<flowSize;i++)
        {
            String flowId=f[i].flowId;
            for(int j=0;j<noOfCounterArrays;j++)
            {
                int hashvalue = flowId.hashCode();
                hashvalue=(hashvalue^s[j]);
                int msb=(hashvalue>>>31)&1;
                hashvalue=Math.abs(hashvalue)%counterSize;
                if(msb==1) {
                    counterMinArray[j][hashvalue] += f[i].size;
                }
                else
                {
                    counterMinArray[j][hashvalue]-=f[i].size;
                }
            }
        }
        int error=0;
        for(i=0;i<flowSize;i++)
        {
            String flowId = f[i].flowId;

            int temparr[]=new int[noOfCounterArrays];
            for (int j = 0; j < noOfCounterArrays; j++) {
                int hashvalue = flowId.hashCode();
                hashvalue=(hashvalue^s[j]);
                int msb=(hashvalue>>>31)&1;
                hashvalue=Math.abs(hashvalue)%counterSize;
                if(msb==1) {
                   temparr[j]=counterMinArray[j][hashvalue];
                }
                else
                {
                    temparr[j]=-counterMinArray[j][hashvalue];
                }
            }
            Arrays.sort(temparr);
            f[i].estimatedSize=temparr[noOfCounterArrays/2];
            error+=Math.abs(f[i].estimatedSize-f[i].size);
        }
        double avgError=((double)error)/(f.length);
        StringBuilder sb=new StringBuilder();
        sb.append("Average error of all the flows : "+avgError+"\n");
        sb.append("Flow Id \t Estimated Size \t Actual Size\n");
        f=sortArray(f);
        for( i=0;i<100;i++)
        {
            sb.append(f[i].flowId+"\t"+f[i].estimatedSize+"\t"+f[i].size+"\n");
        }

        generateOutPutFiles("counterSketch.txt",sb);


    }

    private static void generateCountMin(int noOfCounterArrays, int counterSize) throws IOException{
        int[] s = generateRandomNumbers(noOfCounterArrays);
        File file = new File("project3input.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        int flowSize = Integer.parseInt(br.readLine());
        Flow f[]=new Flow[flowSize];
        String str;
        int i=0;
        while((str = br.readLine()) != null) {
            String[] st = str.split("\\s+");
            f[i++] = new Flow(st[0],st[1]);
        }

        int counterMinArray[][]=new int[noOfCounterArrays][counterSize];
        for(i=0;i<flowSize;i++)
        {
            String flowId=f[i].flowId;
            for(int j=0;j<noOfCounterArrays;j++)
            {
                int hashvalue=flowId.hashCode();
                hashvalue=Math.abs(hashvalue^s[j])%counterSize;
                counterMinArray[j][hashvalue]+=f[i].size;
            }
        }
        int error=0;
        for(i=0;i<flowSize;i++) {
            String flowId = f[i].flowId;
            int minValue = Integer.MAX_VALUE;
            for (int j = 0; j < noOfCounterArrays; j++) {
                int hashvalue = flowId.hashCode();
                 hashvalue=Math.abs(hashvalue^s[j])%counterSize;
                minValue = Math.min(minValue, counterMinArray[j][hashvalue]);
            }
            f[i].estimatedSize=minValue;
            error+=Math.abs(minValue-f[i].size);
        }
        double avgError=((double)error)/(f.length);
        StringBuilder sb=new StringBuilder();
        sb.append("Average error of all the flows : "+avgError+"\n");
        sb.append("Flow Id \t Estimated Size \t Actual Size\n");
        f=sortArray(f);
        for( i=0;i<100;i++)
        {
            sb.append(f[i].flowId+"\t"+f[i].estimatedSize+"\t"+f[i].size+"\n");
        }

        generateOutPutFiles("countMinSketch.txt",sb);


    }
    private static void generateOutPutFiles(String fileName,StringBuilder str) {
        BufferedWriter bw = null;
        try {
            bw=new BufferedWriter(new FileWriter(fileName));
            bw.write(String.valueOf(str));
            bw.close();
        }
        catch (Exception ex)
        {
            System.out.println("Exception in file creation");
        }
    }

    private static Flow[] sortArray(Flow[] f) {
        Arrays.sort(
                f,
                (x,y) ->y.estimatedSize -x.estimatedSize
        );
        return f;
    }

    private static int[] generateRandomNumbers(int hashes)
    {
        int[] s=new int[hashes];
        //Using hashset to generate the unique values
        HashSet<Integer> hs=new HashSet<>();
        int i=0;
        while(i<hashes)
        {
            int temp=(int)(Math.random() * 1000) + 1;
            if(!hs.contains(temp))
            {
                s[i]=temp;
                i++;
            }
            hs.add(temp);
        }
        return s;
    }
}
