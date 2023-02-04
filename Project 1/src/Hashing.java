import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

public class Hashing {
    public static  void main(String args[])
    {
        Scanner s=new Scanner(System.in);
        System.out.println("Enter the Number of table entries : ");
        int tableEntries=s.nextInt();
        System.out.println("Enter the Number of flows : ");
        int flows=s.nextInt();
        // Generating random flow id's
        int[] flowIds=generateFlowIds(flows);
        System.out.println("Enter one of the following : \n Multi-Hashing \n CuckooHashing \n D-leftHashing \n"+"Enter the type of hashing  :");
        String hashType=s.next();
        int hasharray[]=new int[tableEntries];
        int hashes;
        switch(hashType.toUpperCase().trim())
        {
            case "MULTI-HASHING":
                System.out.println(" Enter the number of hashes : ");
                hashes=s.nextInt();
                //code to execute Multi Hashing
                generateMultiHashing(tableEntries,hashes,flowIds,hasharray)  ;
                break;
            case "CUCKOOHASHING":
                System.out.println(" Enter the number of hashes : ");
                hashes=s.nextInt();
                System.out.println("Enter the number of cuckoo steps : ");
                int cuckooSteps=s.nextInt();
                // Code to generate Cuckoo Hashing
                generateCuckooHashing(tableEntries,hashes,flowIds,hasharray,cuckooSteps);
                break;
            case "D-LEFTHASHING":
                System.out.println(" Enter the number of segments : ");
                int segments=s.nextInt();
                // Code to generate D-Left Hashing
                generateDLeftHashing(tableEntries,segments,flowIds,hasharray);
            break;
        }
    }
    // method implemented for D-Left hashing
    private static void generateDLeftHashing(int tableEntries, int segments, int[] flowIds,int[] hashArray)
    {
        //generating a random number
        int a=(int)(Math.random() * tableEntries/segments) + 1;
        // choosing W such that a & W are relatively prime
        int W=generateWValue(a,tableEntries/segments);
        int numberOfTableEntries=0;
        for(int i=0;i<flowIds.length;i++)
        {
            x:for(int j=0;j<segments;j++) {

                int hashValue=(int)Math.floor(((a*(flowIds[i]))%W)/((double)W/tableEntries));
                hashValue=Math.abs(hashValue);
                // if flowid is already present in the hash array go to the next flow id
                if(hashArray[(tableEntries*j+hashValue)/segments]==flowIds[i])
                {
                    numberOfTableEntries++;
                    break x;
                }
                if(hashArray[(tableEntries*j+hashValue)/segments]==0)
                {
                    hashArray[(tableEntries*j+hashValue)/segments]=flowIds[i];
                    numberOfTableEntries++;
                    break x;
                }
            }
        }
        //code to generate the output file
        StringBuilder str=new StringBuilder();
        String st="No of flows in the table : "+flowIds.length+"\nNo of flows in the hash table : "+numberOfTableEntries+"\n";
        str.append(st);
        str.append("Flow Id  - Hash Table index \n");
        for(int i=0;i<hashArray.length;i++)
        {
            str.append(hashArray[i]+" -  "+i+"\n");
        }
        generateOutPutFiles("dLeftHashing.txt",str,numberOfTableEntries);
    }

    private static void generateCuckooHashing(int tableEntries, int hashes, int[] flowIds, int[] hashArray,int cuckooSteps)
    {
        //generating random numbers for  3 different hashes
        int[] s=generateRandomNumbers(hashes);
        //generating a random a value
        int a=(int)(Math.random() * tableEntries) + 1;
        //  code to generate W value such that a & W are relatively prime
        int W=generateWValue(a,tableEntries);
        int numberOfTableEntries=0;
        for(int i=0;i<flowIds.length;i++)
        {
            boolean flag=false;
            x:for (int j = 0; j < hashes; j++)
            {
                int hashValue=(int)Math.floor(((a*(s[j]^flowIds[i]))%W)/((double)W/tableEntries));
                hashValue=Math.abs(hashValue);
                if(hashArray[hashValue]==flowIds[i])
                {
                    flag=true;
                    numberOfTableEntries++;
                    break x;
                }
                if(hashValue<tableEntries && hashArray[hashValue]==0) {
                    hashArray[hashValue] = flowIds[i];
                    numberOfTableEntries++;
                    flag=true;
                    break x;
                }
            }
            if(flag==false)
            {
                x:for (int j = 0; j < hashes; j++) {
                    int hashValue=(int)Math.floor(((a*(s[j]^flowIds[i]))%W)/((double)W/tableEntries));
                    hashValue=Math.abs(hashValue);

                if(hashValue<tableEntries && move(hashArray[hashValue],cuckooSteps,hashArray,s,a,W)==true)
                    {
                        hashArray[hashValue]=flowIds[i];
                        numberOfTableEntries++;
                        break x;
                    }
                }

            }
        }
        StringBuilder str=new StringBuilder();
        String st="No of flows : "+flowIds.length+"\nNo of flows in the hash table : "+numberOfTableEntries+"\n";
        str.append(st);
        str.append("Flow Id  - Hash Table index \n");
        for(int i=0;i<tableEntries;i++)
        {
                str.append(hashArray[i]+" -  "+i+"\n");
        }
        generateOutPutFiles("cuckooHashing.txt",str,numberOfTableEntries);
    }
    // code to implement cuckoo steps
    private static boolean move(int flowId, int  cuckooSteps,int[] hashArray, int[] s,int a,int W) {
        for(int j=0;j<s.length;j++)
        {
            int hashValue=(int)Math.floor(((a*(s[j]^flowId))%W)/((double)W/hashArray.length));
            hashValue=Math.abs(hashValue);
            if(hashValue<hashArray.length && hashArray[hashValue]==0) {
                hashArray[hashValue] = flowId;
                return true;
            }

        }
        if(cuckooSteps>1)
        {
            for(int j=0;j<s.length;j++) {
                int hashValue=(int)Math.floor(((a*(s[j]^flowId))%W)/((double)W/hashArray.length));
                hashValue=Math.abs(hashValue);
                if(hashValue<hashArray.length && move(hashArray[hashValue],cuckooSteps-1,hashArray,s,a,W))
                {
                return true;
                }
            }
        }
        return false;
    }
    private static void generateMultiHashing(int tableEntries, int hashes, int[] flowIds,int[] hashArray)
    {
        //generating random values for 3 different hashes
        int[] s=generateRandomNumbers(hashes);
        // generating a random a values
        int a=(int)(Math.random() * tableEntries) + 1;
        // choosing W such that a & W are relatively prime
        int W=generateWValue(a,tableEntries);
        int numberOfTableEntries=0;
        for(int i=0;i<flowIds.length;i++)
        {
            x:for(int j=0;j<hashes;j++)
            {
                int hashValue=(int)Math.floor(((a*(s[j]^flowIds[i]))%W)/((double)W/tableEntries));
                hashValue=Math.abs(hashValue);
                if(hashArray[hashValue]==flowIds[i])
                {
                    numberOfTableEntries++;
                    break x;
                }
                if(hashArray[hashValue]==0)
                {
                 hashArray[hashValue]=flowIds[i];
                 numberOfTableEntries++;
                 break x;
                }
            }
        }
        StringBuilder str=new StringBuilder();
        String st="No of flows : "+flowIds.length+"\nNo of flows in the hash table : "+numberOfTableEntries+"\n";
        str.append(st);
        str.append("Flow Id  - Hash Table index \n");
        for(int i=0;i<tableEntries;i++) {
            str.append(hashArray[i] + " -  " + i + "\n");
        }
        generateOutPutFiles("multi-hashing.txt",str,numberOfTableEntries);
    }
    // code to generate output files
    private static void generateOutPutFiles(String fileName,StringBuilder str, int numberOfEntries) {
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
    // code to generate W value
    private static int generateWValue(int a, int tableEntries)
    {
        int W=(int)(Math.random() * tableEntries) + 1;
        while(gcd(a,W)!=1)
        {
            W=(int)(Math.random() * tableEntries) + 1;
        }
        return W;
    }
    // Calculate gcd
    public static int gcd(int a,int W)
    {
        if (W == 0) {
            return a;
        }
        return gcd(W, a % W);
    }
    // code to create flow id's
    private static int[] generateFlowIds(int flows)
    {
        int[] flowIds=new int[flows];
        for(int i=0;i<flows;i++)
        {
            flowIds[i]=(int)(Math.random() * Integer.MAX_VALUE) + 1;
        }
        return flowIds;
    }
    // code to generate random numbers
    private static int[] generateRandomNumbers(int hashes)
    {
        int[] s=new int[hashes];
        //Using hashset to generate the unique values for hashing
        HashSet<Integer>hs=new HashSet<>();
        int i=0;
        while(i<hashes)
        {
            int temp=(int)(Math.random() * 10000) + 1;
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
