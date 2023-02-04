import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Scanner;

public class Filters {
    public static  void main(String args[])
    {
        Scanner s=new Scanner(System.in);
        System.out.println("Enter the type of filtering  : ");
        String filterType=s.next();
        int hashes;
        int noOfBits;
        int A[];
        int noOfElements;
        switch(filterType.toUpperCase().trim())
        {
            case "COUNTMIN":
                System.out.println("Enter the Number of counter arrays : ");
                noOfElements =s.nextInt();
                A=generateElements(noOfElements);
                System.out.println("Enter the Number of bits in the filter : ");
                noOfBits=s.nextInt();
                System.out.println("Enter the number of hashes : ");
                hashes=s.nextInt();
                int[] B=generateElements(noOfElements);
                bloomFilter(noOfBits,hashes,A,B)  ;
                break;
            case "COUNTING_BLOOM_FILTER":
                System.out.println("Enter the Number of elements to be encoded : ");
                noOfElements =s.nextInt();
                A=generateElements(noOfElements);
                System.out.println("Enter the Number of bits in the filter : ");
                noOfBits=s.nextInt();
                System.out.println("Enter the number of hashes : ");
                hashes=s.nextInt();
                System.out.println("Enter the number of elements to be deleted from filter :");
                int countOfDeletedElements=s.nextInt();
                System.out.println("Enter the number of elements to be added to the filter");
                int elementsToAdd=s.nextInt();
                countingBloomFilter(noOfBits,hashes,A,countOfDeletedElements,elementsToAdd);
                break;
            case "CODED_BLOOM_FILTER":
                System.out.println("Enter the number of sets");
                int setcount=s.nextInt();
                System.out.println("Enter the Number of elements to be encoded : ");
                noOfElements =s.nextInt();
                System.out.println("Enter the number of filters : ");
                int filterCount=s.nextInt();
                System.out.println("Enter the Number of bits in the filter : ");
                noOfBits=s.nextInt();
                System.out.println("Enter the number of hashes : ");
                hashes=s.nextInt();
                codedBloomFilter(setcount,noOfElements,filterCount,noOfBits,hashes);
                break;
        }
    }
    private static void bloomFilter(int noOfBits, int hashes, int[] A,int[] B) {
        int[] s = generateRandomNumbers(hashes);
        int a = (int) (Math.random() * noOfBits) + 1;
        int W = generateWValue(a, noOfBits);
        int [] BitArray=new int[noOfBits];
        StringBuilder str=new StringBuilder();
        for(int i=0;i<A.length;i++)
        {
            x:for(int j=0;j<hashes;j++)
            {
                int hashValue=(int)Math.floor(((a*(s[j]^A[i]))%W)/((double)W/noOfBits));
                hashValue=Math.abs(hashValue);
                BitArray[hashValue]=1;
            }
        }
        int noOfEntriesOfA=0;
        for(int i=0;i<A.length;i++)
        {
            int f=1;
            x:for(int j=0;j<hashes;j++) {
                int hashValue=(int)Math.floor(((a*(s[j]^A[i]))%W)/((double)W/noOfBits));
                hashValue=Math.abs(hashValue);
                if(BitArray[hashValue]==0)
                {
                    f=0;
                    break x;
                }
            }
            if(f==1)
            {
                noOfEntriesOfA++;
            }
        }
        str.append("No of elements of A in the filter is : "+noOfEntriesOfA+" \n");
        int noOfEntriesOfB=0;
        for(int i=0;i<B.length;i++)
        {
            int f=1;
            x:for(int j=0;j<hashes;j++) {
                int hashValue=(int)Math.floor(((a*(s[j]^B[i]))%W)/((double)W/noOfBits));
                hashValue=Math.abs(hashValue);
                if(BitArray[hashValue]==0)
                {
                    f=0;
                    break x;
                }
            }
            if(f==1)
            {
                noOfEntriesOfB++;
            }
        }
        str.append("No of elements of B in the filter is : "+noOfEntriesOfB+" \n");
        generateOutPutFiles("bloom_filter.txt",str);
    }
    private static void countingBloomFilter(int noOfBits, int hashes, int[] A,int elementsRemoved, int elementsAdded) {
        int[] s = generateRandomNumbers(hashes);
        int a = (int) (Math.random() * noOfBits) + 1;
        int W = generateWValue(a, noOfBits);
        int [] hashArray=new int[noOfBits];
        StringBuilder str=new StringBuilder();
        for(int i=0;i<A.length;i++)
        {
            x:for(int j=0;j<hashes;j++)
            {
                int hashValue=(int)Math.floor(((a*(s[j]^A[i]))%W)/((double)W/noOfBits));
                hashValue=Math.abs(hashValue);
                hashArray[hashValue]+=1;
            }
        }
        int noOfEntriesOfA=0;
        int C[]=generateElements(elementsAdded);
        int k=0;
        x:for(int i=0;i<A.length;i++)
        {
            if(i>elementsRemoved)
            {
                break x;
            }
            for(int j=0;j<hashes;j++) {
                int hashValue=(int)Math.floor(((a*(s[j]^A[i]))%W)/((double)W/noOfBits));
                hashValue=Math.abs(hashValue);
                hashArray[hashValue]--;
                }
        }
        for(int i=0;i<C.length;i++)
        {
            for(int j=0;j<hashes;j++) {
                int hashValue=(int)Math.floor(((a*(s[j]^C[i]))%W)/((double)W/noOfBits));
                hashValue=Math.abs(hashValue);
                hashArray[hashValue]+=1;
            }
        }
        for(int i=0;i<A.length;i++)
        {
            int f=1;
            x:for(int j=0;j<hashes;j++) {
                int hashValue=(int)Math.floor(((a*(s[j]^A[i]))%W)/((double)W/noOfBits));
                hashValue=Math.abs(hashValue);
                if(hashArray[hashValue]<=0)
                {
                    f=0;
                }
            }
            if(f==1)
            {
                noOfEntriesOfA++;
            }
        }
        str.append("No of elements of A in the filter is : "+noOfEntriesOfA+" \n");
        generateOutPutFiles("counting_bloom_filter.txt",str);
    }
    private static void codedBloomFilter(int setcount, int noOfElements,int filterCount,int noOfBits,int hashes) {
        int[] s = generateRandomNumbers(hashes);
        int a = (int) (Math.random() * noOfBits) + 1;
        int W = generateWValue(a, noOfBits);
        int A[][]=generate2DElements(setcount,noOfElements);
        int filterArray[][]=new int[filterCount][noOfBits];
        StringBuilder str=new StringBuilder();
       for(int i=0;i<A.length;i++)
       {
           for(int j=0;j<A[i].length;j++)
           {
               for(int k=0;k<s.length;k++)
               {
                   int elementId=A[i][j]^s[k];
                   int hashValue=(int)Math.floor(((a*(elementId))%W)/((double)W/noOfBits));
                   hashValue=Math.abs(hashValue);
                   String st=Integer.toBinaryString(i+1);
                   for(int m=st.length()-1;m>=0;m--)
                   {
                       if(st.charAt(m)=='1')
                       {
                           System.out.println(st.length()-m-1);
                           filterArray[st.length()-m-1][hashValue]=1;
                       }
                   }
               }
           }
       }
       int numberOfEntries=0;
        for(int i=0;i<A.length;i++) {

            for (int j = 0; j < A[0].length; j++) {
                int f=1;
                int f2=0;
                x:for (int k = 0; k < s.length; k++) {
                    int elementId = A[i][j] ^ s[k];
                    int hashValue = (int) Math.floor(((a * (elementId)) % W) / ((double) W / noOfBits));
                    hashValue=Math.abs(hashValue);
                    String st = Integer.toBinaryString(i+1);
                    while(st.length()<filterCount)
                    {
                     st='0'+st;
                    }
                    for (int m = filterCount-1; m >= 0; m--) {

                            if(st.charAt(m) == '1' && filterArray[st.length()-m-1][hashValue]==0)
                            {
                                f=0;
                                break x;
                            }
                            if(st.charAt(m)=='0' && filterArray[st.length()-m-1][hashValue]==1)
                            {
                                f2++;
                            }


                    }
                }
                if(f==1 && f2!=hashes)
                {
                    numberOfEntries++;
                }
            }
        }
        str.append("No of elements of A in the filter with the correct lookup is : "+numberOfEntries+" \n");
        generateOutPutFiles("coded_bloom_filter.txt",str);

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

    private static int[] generateRandomNumbers(int hashes)
    {
        int[] s=new int[hashes];
        //Using hashset to generate the unique values
        HashSet<Integer> hs=new HashSet<>();
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
    private static int[][] generate2DElements(int setCount,int noOfElements)
    {
        int A[][]=new int[setCount][noOfElements];
        HashSet<Integer>hs=new HashSet<>();
        for(int i=0;i<setCount;i++)
        {
            for(int j=0;j<noOfElements;j++)
            {
                int rand=(int)(Math.random() * Integer.MAX_VALUE) + 1;
                while(hs.contains(rand))
                {
                    rand=(int)(Math.random() * Integer.MAX_VALUE) + 1;
                }
                hs.add(rand);
                A[i][j]=rand;
            }
        }
        return A;
    }
    private static int[] generateElements(int flows)
    {
        int[] elements=new int[flows];

        for(int i=0;i<flows;i++)
        {
            elements[i]=(int)(Math.random() * Integer.MAX_VALUE) + 1;
        }
        return elements;
    }
}
